MGU_AbstractModule {

	classvar instance_count;

	var <out, <>server, <num_inputs, <num_outputs, <>name;
	var <type, <this_instance;
	var <inbus, <>node_group, <master_internal;
	var <level, <mix, <pan;

	var <>sends_only;
	var <def, <master_def;

	var <master_container, <sends_container, <container;
	var senddef_array, <sendlevel_array, <send_array;
	var node_array_send, node_array_master, <node_array;

	var <gui, <>description;

	*new { |out = 0, server, num_inputs, num_outputs, name|
		^this.newCopyArgs(out, server, num_inputs, num_outputs, name)
	}

	init {

		// count
		instance_count !? { instance_count = instance_count + 1 };
		instance_count ?? { instance_count = 1 };
		this_instance = instance_count;

		// defaults
		server ?? { server = Server.default };
		name ?? {
			var classname = this.class.asCompileString;
			if(classname.split($_).size() == 2) {
				name = classname.split($_)[1] ++ "_" ++ this_instance } { // else
				name = classname;
			}
		};

		// init node arrays
		node_group = Group(1, 'addToTail');
		node_array = [];
		node_array_send = [];
		node_array_master = [];

		// internal master parameters
		sends_only = false;
		container = MGU_container(name, nil, node_group, 3127);
		master_container = MGU_container("master", container, node_group, 3127);

		level = MGU_parameter(master_container, \level, Float, [-96, 12], 0, true, \dB, \amp);
		master_internal = Bus.audio(server, num_outputs);

		// type inits
		switch(type)
		{\effect} {
			inbus = Bus.audio(server, num_inputs);
			mix = MGU_parameter(master_container, \mix, Float, [0, 1], 0.5, true)}
		{\analyzer} {
			inbus = Bus.audio(server, num_inputs)}
		{\mts_generator} {
			pan = MGU_parameter(master_container, \pan, Float, [-1, 1], 0, true)};

	}

	// CUSTOM SETTERS

	inbus_{ |newbus| // accessed by effect racks only
		inbus.free();
		inbus = newbus;
		this.initMasterDef();
	}

	out_ { |new_out|
		out = new_out;
		this.initMasterDef()
	}

	num_outputs_ {|v| // in case of multichannel expansion
		num_outputs = v;
		master_internal.free();
		master_internal = Bus.audio(server, num_outputs);
		this.initMasterDef();
	}

	type_ { |val| // weird but auto setter
		// doesn't seem to work when initing types directly after constructor
		type = val;
	}

	// OSC MODULE MANAGEMENT

	registerToMinuit { |minuitInterface|
		minuitInterface.addContainer(this.container);
		this.container.parentContainer = minuitInterface;
	}

	includeIn { |parent|
		parent.container.registerContainer(this.container);
		this.node_group.free;
		this.node_group = parent.node_group;
	}

	// DEFS & SYNTHS

	initMasterDef { // this has to be simplified...

		var reply_address = '/' ++ name ++ '/reply'; // <- for vu meters

		switch(type)

		{\mts_generator} { // mono to stereo -> panning implementation
			master_def = SynthDef(name ++ "_master", {
				var in = In.ar(master_internal, num_outputs);
				var pan = Pan2.ar(in, pan.kr);
				var process = pan * level.kr;
				var reply = SendPeakRMS.kr(process, 20, 3, reply_address);
				Out.ar(out, process)}).add
		}

		{\generator} {
			master_def = SynthDef(name ++ "_master", {
				var in = In.ar(master_internal, num_outputs);
				var process = in * level.kr;
				var reply = SendPeakRMS.kr(process, 20, 3, reply_address);
				Out.ar(out, process)}).add
		}

		{\effect} {
			master_def = SynthDef(name ++ "_master", {
				var in_wet = In.ar(master_internal, num_outputs);
				var in_dry = In.ar(inbus, num_inputs);
				var process = FaustDrywet.ar(in_dry, in_wet, mix.kr) * level.kr;
				var reply = SendPeakRMS.kr(process, 20, 3, reply_address);
				var reply_dry = SendPeakRMS.kr(in_dry, 20, 3, reply_address ++ '/input');
				Out.ar(out, process)}).add
		};

	}

	sendSynth {

		// create master
		if(((type == \generator) || (type == \effect)) && (sends_only == false)) {
		node_array_master = node_array_master.add(Synth(name ++ "_master",
			master_container.makeSynthArray.asOSCArgArray, node_group, 'addToTail'));
		};

		// create main node
		node_array = node_array.add(Synth(name, container.makeSynthArray.asOSCArgArray,
			node_group, 'addToHead'));

		// create sends
		send_array !? {
			send_array.size.do({|i|
				node_array_send = node_array_send.add(
					Synth(name ++ "_send" ++ (i+1),
						sends_container.makeSynthArray.asOSCArgArray, node_group, 'addToTail'));
			})
		};

		// send children synths

		this.instVarSize.do({|i|
			if(this.instVarAt(i).class.superclasses.includes(MGU_AbstractModule))
			{ this.instVarAt(i).sendSynth() }
		});

		// refresh module gui if needed
		//gui !? { gui.sendsynth_button.current_state_(1) };
	}

	killSynth { |index|
		node_array_master[index].free;
		node_array_master.removeAt(index);
		node_array[index].free;
		node_array.removeAt(index);
	}

	killAllSynths {

		node_array.size.do({|i| // free synth nodes
			node_array[0].free;
			node_array.removeAt(0);
		});

		node_array_master.size.do({|i| // free master nodes
			node_array_master[0].free;
			node_array_master.removeAt(0);
		});

		node_array_send.size.do({|i| // free send nodes
			node_array_send[0].free;
			node_array_send.removeAt(0);
		});

		//gui !? { gui.sendsynth_button.current_state_(0) };

	}

	addNewSend { |target, fader_mode = \prefader, fx_mode = \postfx|
		// pre-post master fader to be tested

		send_array ?? { send_array = [] };
		sendlevel_array ?? {
			sendlevel_array = [];
			sends_container = MGU_container("sends", container, node_group, 3127);
		};

		senddef_array ?? { senddef_array = [] };

		send_array = send_array.add(target);

		sendlevel_array = sendlevel_array.add(
			MGU_parameter(sends_container, "snd_" ++ target.name,
				Float, [-96, 12], 0, true, \dB, \amp));

		if(type == \effect)
		{ this.generateSendSynthDefFromEffect(target, fader_mode, fx_mode) }
		{ this.generateSendSynthDef(target, fader_mode) };

	}

	generateSendSynthDefFromEffect { |target, fader_mode, fx_mode| // this has to be simplified...

		case

		{ (fader_mode == \prefader) && (fx_mode == \prefx) }
		{ senddef_array = senddef_array.add(
			SynthDef(name ++ "_send" ++ send_array.size, {
				var in = In.ar(master_internal, num_outputs);
				var process = in * sendlevel_array[sendlevel_array.size - 1].kr;
				Out.ar(target.inbus, process);
			}).add;
		)}

		{ (fader_mode == \postfader) && (fx_mode == \postfx) }
		{ senddef_array = senddef_array.add(
			SynthDef(name ++ "_send" ++ send_array.size, {
				var in_dry = In.ar(inbus, num_inputs);
				var in_wet = In.ar(master_internal, num_outputs);
				var process = FaustDrywet.ar(in_dry, in_wet, mix.kr)
				* sendlevel_array[sendlevel_array.size - 1].kr * level.kr;
				Out.ar(target.inbus, process);
			}).add;
		)}

		{ (fader_mode == \prefader) && (fx_mode == \postfx) }
		{ senddef_array = senddef_array.add(
			SynthDef(name ++ "_send" ++ send_array.size, {
				var in_dry = In.ar(inbus, num_inputs);
				var in_wet = In.ar(master_internal, num_outputs);
				var process = FaustDrywet.ar(in_dry, in_wet, mix.kr)
				* sendlevel_array[sendlevel_array.size - 1].kr;
				Out.ar(target.inbus, process);
			}).add;
		)}

		{ (fader_mode == \postfader) && (fx_mode == \prefx) }
		{ senddef_array = senddef_array.add(
			SynthDef(name ++ "_send" ++ send_array.size, {
				var in = In.ar(master_internal, num_outputs);
				var process = in * sendlevel_array[sendlevel_array.size - 1].kr * level.kr;
				Out.ar(target.inbus, process);
			}).add;
		)};

	}

	generateSendSynthDef { |target, fader_mode|

		switch(fader_mode)

		{\prefader} {
			senddef_array = senddef_array.add(
				SynthDef(name ++ "_send" ++ send_array.size, {
					var in = In.ar(master_internal, num_outputs);
					var process = in * sendlevel_array[sendlevel_array.size -1].kr;
					Out.ar(target.inbus, process);
				}).add;
		)}

		{\postfader} { senddef_array = senddef_array.add(
				SynthDef(name ++ "_send" ++ send_array.size, {
					var in = In.ar(master_internal, num_outputs);
					var process = in * sendlevel_array[sendlevel_array.size -1].kr * level.kr;
					Out.ar(target.inbus, process);
				}).add;
		)};

	}

	// MODULE CONNEXIONS

	connectToModule { |module, replace = false| // replace argument tbi
		this.out_(module.inbus.index);
		this.initMasterDef();
	}

	connectToParameter { |module, parameter, type = \control| // also tbi
		parameter.enableModulation(server, type);

		fork({
			server.sync();
			def.add;
			module.def.add;
		});

	}

	// USER INTERFACE

	generateUI { |alwaysOnTop = false|
		gui = MGU_moduleGUI(this);
	}

	// CONTROLLERS

	pushLearn { // this doesn't work for containers with >=9 parameters...
		container.paramAccesses.do({|param, i|
			param.pushLearnResponder(71+i);
		});

	}

	// PRESET SUPPORT // JSON TBI with concretely defined parameter names and matching check

	getPresetFolderPath {
		var folder_path;
		folder_path = Platform.userExtensionDir +/+ "mgulibSC/classes/modules/presets" +/+
		this.class.asCompileString;
		folder_path = folder_path.standardizePath();
		^folder_path
	}

	getPresetFilePath { |fileName|
		var file_path;
		file_path = this.getPresetFolderPath +/+ fileName ++ ".txt";
		file_path = file_path.standardizePath();
		^file_path
	}

	saveState { |fileName|
		var folder_path = this.getPresetFolderPath;
		var file_path = this.getPresetFilePath(fileName);
		var stateArray = [];
		var stateFile;
		this.instVarSize.do({|i|
			if(this.instVarAt(i).class == MGU_parameter) {
				this.instVarAt(i).val.postln;
				stateArray = stateArray.add(this.instVarAt(i).val(true));
			};
		});

		// if path doesn't exist, create path
		if(PathName(folder_path).isFolder == false) {
			folder_path.mkdir;
			("module: preset folder created at" + folder_path).postln;
		};

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
			if(this.instVarAt(i).class == MGU_parameter) {
				this.instVarAt(i).val = stateArray[j];
				j = j+1;
			};
		});
	}

	getAvailablePresets { // returns preset list for this module
		var folder_path = this.getPresetFolderPath(), entries = [];
		PathName(folder_path).filesDo({|file|
			file = file.fileName.split($.);
			if(file[1] == "txt") {entries = entries.add(file[0])}
		});
		^entries
	}

	deletePreset { |file_name|
		var file_path = this.getPresetFilePath(file_name);
		("rm -rf" + "\"" ++ file_path ++ "\"").unixCmd;
	}

}


	