MGU_AbstractModule {

	classvar instanceCount;

	var <out, <>server, <numInputs, <numOutputs, <>name;
	var <type, <thisInstance;
	var <inbus, <container, <>nodeGroup, <master_internal;
	var <level, <mix, <pan;

	var <def, <master_def;

	var sendDefArray, <sendLevelArray, <sendArray;
	var nodeArray_send, nodeArray_master, <nodeArray;

	var <gui, <description;

	*new { |out = 0, server, numInputs, numOutputs, name|
		^this.newCopyArgs(out, server, numInputs, numOutputs, name)
	}

	init {

		// count
		instanceCount !? { instanceCount = instanceCount + 1 };
		instanceCount ?? { instanceCount = 1 };
		thisInstance = instanceCount;

		// defaults
		server ?? { server = Server.default };
		name ?? {name = this.class.asCompileString.split($_)[1] ++ "_" ++ thisInstance };

		// node arrays
		nodeGroup = Group(1, 'addToTail');
		nodeArray = [];
		nodeArray_send = [];
		nodeArray_master = [];

		// internal master parameters
		container = MGU_container(name, nil, nodeGroup, 3127, this);
		level = MGU_parameter(container, \level, Float, [-96, 12], 0, true, \dB, \amp);
		master_internal = Bus.audio(server, numOutputs);

		// type inits
		switch(type)
		{\effect} {
			inbus = Bus.audio(server, numInputs);
			mix = MGU_parameter(container, \mix, Float, [0, 1], 0.5, true)}
		{\mts_generator} {
			pan = MGU_parameter(container, \pan, Float, [-1, 1], 0, true)};

	}

	// CUSTOM SETTERS

	inbus_{ |newbus|
		inbus.free();
		inbus = newbus;
		this.initMasterDef();
	}

	out_ { |newOut|
		out = newOut;
		this.initMasterDef()
	}

	numOutputs_ {|v|
		numOutputs = v;
		master_internal.free();
		master_internal = Bus.audio(server, numOutputs);
	}

	type_ { |val| // weird but auto setter
		// doesn't seem to work when initing types directly after constructor
		type = val;
	}

	// OSC MODULE MANAGEMENT

	registerToMinuit { |minuitInterface|
		minuitInterface.addContainer(container);
		container.parentContainer = minuitInterface;
	}

	includeIn { |parent|
		parent.container.registerContainer(this.container);
		this.nodeGroup.free;
		this.nodeGroup = parent.nodeGroup;
	}

	// DEFS & SYNTHS

	initMasterDef {

		switch(type)

		{\mts_generator} { // mono to stereo -> panning implementation
				master_def = SynthDef(name ++ "_master", {
					var in = In.ar(master_internal, numOutputs);
					var pan = Pan2.ar(in, pan.kr);
					var process = pan * level.kr;
					var reply = SendPeakRMS.kr(process, 20, 3, '/' ++ name ++ '/reply');
					Out.ar(out, process)}).add
		}

		{\generator} {
				master_def = SynthDef(name ++ "_master", {
					var in = In.ar(master_internal, numOutputs);
					var process = in * level.kr;
					var reply = SendPeakRMS.kr(process, 20, 3, '/' ++ name ++ '/reply');
					Out.ar(out, process)}).add
		}

		{\effect} {
				master_def = SynthDef(name ++ "_master", {
					var in_wet = In.ar(master_internal, numOutputs);
					var in_dry = In.ar(inbus, numInputs);
					var process = FaustDrywet.ar(in_dry, in_wet, mix.kr) * level.kr;
					var reply = SendPeakRMS.kr(process, 20, 3, '/' ++ name ++ '/reply');
					Out.ar(out, process)}).add
		};

	}

	sendSynth {

		// to implement : auto differentiation between master node and synth nodes (with include in ?)

		switch(type)

		{\effect} {
			nodeArray_master = nodeArray_master.add(Synth(name ++ "_master",
				[name ++ "_level", level.val, name ++ "_mix", mix.val],
				nodeGroup, 'addToTail'))
		}

		{\generator} {
			nodeArray_master = nodeArray_master.add(Synth(name ++ "_master",
				[name ++ "_level", level.val], nodeGroup, 'addToTail'))
		};

		nodeArray = nodeArray.add(Synth(name, container.makeSynthArray.asOSCArgArray,
			nodeGroup, 'addToHead'));

		sendArray !? {
			sendArray.size.do({|i|
				nodeArray_send = nodeArray_send.add(
					Synth(name ++ "_send" ++ (i+1), [name ++ "_snd_" ++ sendArray[i].name,
						sendLevelArray[i].val], nodeGroup, 'addToTail'))
			})
		};

		// send children synths

		this.instVarSize.do({|i|
			if(this.instVarAt(i).class.superclass == MGU_AbstractModule)
			{ this.instVarAt(i).sendSynth() }
		});

		// refresh module gui if needed
		gui !? { gui.sendsynth_button.current_state_(1) };
	}

	killSynth { |index|
		nodeArray_master[index].free;
		nodeArray_master.removeAt(index);
		nodeArray[index].free;
		nodeArray.removeAt(index);
	}

	killAllSynths {

		nodeArray.size.do({|i| // free synth nodes
			nodeArray[0].free;
			nodeArray.removeAt(0);
		});

		nodeArray_master.size.do({|i| // free master nodes
			nodeArray_master[0].free;
			nodeArray_master.removeAt(0);
		});

		nodeArray_send.size.do({|i| // free send nodes
			nodeArray_send[0].free;
			nodeArray_send.removeAt(0);
		});

		gui !? { gui.sendsynth_button.current_state_(0) };

	}

	addNewSend { |target, mode = \prefader| // pre-post fader to be implemented

		sendArray ?? { sendArray = [] };
		sendLevelArray ?? { sendLevelArray = [] };
		sendDefArray ?? { sendDefArray = [] };

		sendArray = sendArray.add(target);

		sendLevelArray = sendLevelArray.add(
			MGU_parameter(container, "snd_" ++ target.name,
				Float, [-96, 12], 0, true, \dB, \amp));

		sendDefArray = sendDefArray.add(
			SynthDef(name ++ "_send" ++ sendArray.size, {
				var in = In.ar(master_internal, numOutputs);
				var process = in * sendLevelArray[sendLevelArray.size -1].kr;
				Out.ar(target.inbus, process);
			}).add;
		);
	}

	// MODULE CONNEXIONS

	connectToModule { |module, replace = false| // replace argument tbi
		this.out_(module.inbus.index);
		this.initMasterDef();
	}

	connectToParameter { |parameter, replace = false| // also tbi
		parameter.enableModulation();
		this.out_(parameter.kbus);
		def.add;
	}

	// SHORTCUTS

	generateUI { |alwaysOnTop = false|
		gui = MGU_moduleGUI(this);
	}

	setDescription { |desc|
		this.container.description = desc;
	}

	// PRESET SUPPORT

	getPresetFolderPath {
		var folder_path;
		folder_path = Platform.userExtensionDir +/+ "mgulibSC/classes/modules/presets" +/+
		this.class.asCompileString;
		folder_path = folder_path.standardizePath;
		^folder_path
	}

	getPresetFilePath { |fileName|
		var file_path;
		file_path = this.getPresetFolderPath +/+ fileName ++ ".txt";
		file_path = file_path.standardizePath;
		^file_path
	}

	saveState { |fileName|
		var folder_path = this.getPresetFolderPath;
		var file_path = this.getPresetFilePath(fileName);
		var stateArray = [];
		var stateFile;
		this.instVarSize.do({|i|
			if(this.instVarAt(i).class == MGU_parameter, {
				this.instVarAt(i).val.postln;
				stateArray = stateArray.add(this.instVarAt(i).val(true));
			});
		});

		// if path doesn't exist, create path
		if(PathName(folder_path).isFolder == false, {
			folder_path.mkdir;
			("module: preset folder created at" + folder_path).postln;
		});

		// create or overwrite text file
		stateFile = File((file_path).standardizePath, "w+");
		stateFile.write(stateArray.asCompileString);
		stateFile.close;
		("module: preset file created at" + file_path).postln;
	}

	recallState { |fileName, interp = false, length = 2000, curve = \lin|

		// + preset interpolation to implement ?
		var folder_path = this.getPresetFolderPath();
		var file_path = this.getPresetFilePath(fileName);
		var stateFile;
		var stateArray = [];
		var j = 0;

		stateFile = File(file_path, "r");
		stateArray = stateFile.readAllString.interpret;

		this.instVarSize.do({|i|
			if(this.instVarAt(i).class == MGU_parameter, {
				this.instVarAt(i).val = stateArray[j];
				j = j + 1;
			})
		});
	}

	availablePresets { // returns preset list for this module -- TBI
		var folder_path = this.getPresetFolderPath(), entries = [];
		PathName(folder_path).filesDo({|file|
			file = file.fileName.split($.);
			if(file[1] == "txt") {entries = entries.add(file[0])}
		});
		^entries
	}

}


	