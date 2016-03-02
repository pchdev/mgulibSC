MGU_AbstractModule {

	classvar instanceCount;

	var <out, <>server, <numInputs, <numOutputs, <>name;
	var <type;
	var <container, type, <master_internal;
	var <inbus, <inbus_index;
	var def;
	var <level, <mix;
	var <thisInstance;
	var sendDefArray, <sendLevelArray, <sendArray;
	var nodeArray_send, nodeArray_master, nodeArray;
	var <>nodeGroup;
	var <master_def;


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

		container = MGU_container(name, nil, nodeGroup, 3127, this);
		level = MGU_parameter(container, \level, Float, [-96, 12], 0, true, \dB, \amp);
		master_internal = Bus.audio(server, numOutputs);

		if(type == \effect) {
			inbus = Bus.audio(server, numInputs);
			mix = MGU_parameter(container, \mix, Float, [0, 1], 0.5, true);
		};

	}

	type_ { |val| // weird but auto setter
		// doesn't seem to work when initing types directly after constructor
		type = val;
	}

	registerToMinuit { |minuitInterface|
		minuitInterface.addContainer(container);
		container.parentContainer = minuitInterface;
	}

	includeIn { |parent|
		parent.container.registerContainer(this.container);
		this.nodeGroup.free;
		this.nodeGroup = parent.nodeGroup;
	}

	inbus_{ |newbus|
		inbus.free();
		inbus = newbus;
		this.initMasterDef();
	}

	initMasterDef {

		switch(type,
			\generator, {
				master_def = SynthDef(name ++ "_master", {
					var in = In.ar(master_internal, numOutputs);
					var process = in * level.kr;
					Out.ar(out, process);
			}).add },
			\effect, {
				master_def = SynthDef(name ++ "_master", {
					var in_wet = In.ar(master_internal, numOutputs);
					var in_dry = In.ar(inbus, numInputs);
					var process = FaustDrywet.ar(in_dry, in_wet, mix.kr) * level.kr;
					Out.ar(out, process);
		}).add });

	}

	sendSynth {

		// if module includes other modules, send them first.

		// and then send this

		switch(type,
			\effect, {
				nodeArray_master = nodeArray_master.add(
					Synth(name ++ "_master", [name ++ "_level", level.val,
						name ++ "_mix", mix.val], nodeGroup, 'addToTail'))},
			\generator, {
				nodeArray_master = nodeArray_master.add(
					Synth(name ++ "_master", [name ++ "_level", level.val],
						nodeGroup, 'addToTail'))});

		nodeArray = nodeArray.add(
			Synth(name, container.makeSynthArray.asOSCArgArray, nodeGroup, 'addToHead'));

		sendArray !? {
			sendArray.size.do({|i|
				nodeArray_send = nodeArray_send.add(
					Synth(name ++ "_send" ++ (i+1), [name ++ "_snd_" ++ sendArray[i].name,
						sendLevelArray[i].val], nodeGroup, 'addToTail'))
			})
		};

		this.instVarSize.do({|i|
			if(this.instVarAt(i).class.superclass == MGU_AbstractModule)
			{ this.instVarAt(i).sendSynth() }
		});


	}

	killSynth { |index|
		nodeArray_master[index].free;
		nodeArray_master.removeAt(index);
		nodeArray[index].free;
		nodeArray.removeAt(index);
	}

	killAllSynths {

		nodeArray.size.do({|i|
			nodeArray[0].free;
			nodeArray.removeAt(0);
		});

		nodeArray_master.size.do({|i|
			nodeArray_master[0].free;
			nodeArray_master.removeAt(0);
		});

		nodeArray_send.size.do({|i|
			nodeArray_send[0].free;
			nodeArray_send.removeAt(0);
		});

	}

	connectToModule { |module, replace = false| // replace argument tbi
		this.out_(module.inbus.index);
		this.initMasterDef();
	}

	connectToParameter { |parameter, replace = false|
		parameter.enableModulation();
		this.out_(parameter.kbus);
		def.add;
	}

	out_ { |newOut|
		out = newOut;
		this.initMasterDef()
	}

	addNewSend { |target|

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

	generateUI { // shortcut
		container.generateUI;
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

		// + preset interpolation to implement
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

	}

}


	