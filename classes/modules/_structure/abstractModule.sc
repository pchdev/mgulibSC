MGU_AbstractModule {

	classvar instanceCount;

	var <out, <>server, <>numChannels, <>name;
	var <def, <container, <nodeGroup, <nodeArray;
	var <inbus;
	var <thisInstance;

	*new { |out = 0, server, numChannels = 1, name|
		^this.newCopyArgs(out, server, numChannels, name)
	}

	init {

		instanceCount !? { instanceCount = instanceCount + 1 };
		instanceCount ?? { instanceCount = 1 };
		thisInstance = instanceCount;
		numChannels ?? { numChannels = 1 };
		out ?? { out = Bus.audio(server, numChannels) };
		server ?? { server = Server.default };
		name ?? { name = this.class.asCompileString.split($_)[1] ++ "_" ++ thisInstance };

		nodeGroup = Group(1, 'addToTail');
		container = MGU_container(name, nil, nodeGroup, 3127, this);
		inbus = MGU_parameter(container, \inbus, Integer, [0, inf], inf, true);

	}

	registerToMinuit { |minuitInterface|
		minuitInterface.addContainer(container);
		container.parentContainer = minuitInterface;
	}

	sendSynth {
		nodeArray = nodeArray.add(
			Synth(name, container.makeSynthArray.asOSCArgArray, nodeGroup, 'addToTail'));
	}

	killSynths {
		nodeArray.size.do({|i|
			nodeArray[0].free;
			nodeArray.removeAt(0);
		});
	}

	connectToModule { |module|
		if(out.class == Bus, { module.inbus.val = out.index }, {
			module.inbus.val = out });
		module.numChannels = numChannels;
	}

	out_ { |numOut|
		numOut !? { out = numOut };
		numOut ?? { out = Bus.audio(server, numChannels) };
	}

	generateUI {
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
				stateArray = stateArray.add(this.instVarAt(i).val);
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


	