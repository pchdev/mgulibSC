MGU_moduleRack : MGU_AbstractModule {

	var <module_array;

	*new { |out = 0, server, numInputs = 2, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\effect)
		.init.initRack.initMasterDef;
	}

	initRack {
		module_array = [];
	}

	addModule { |module|
		module_array = module_array.add(module);
		container.registerContainer(module_array[module_array.size -1].container);

		if(module_array[0].type == \effect) { // rack input = first effect input
			"is effect".postln;
			this.inbus = module_array[0].inbus;
		};

		"before error".postln;

		if(module_array.size > 1) {
			module_array[module_array.size -2].connectToModule(module_array[module_array.size -1]);
		};

		module_array[module_array.size -1].out = master_internal.index;

	}

	addModules { |...moduleArray|
		moduleArray.size.do({|i|
			this.addModule(moduleArray[i]);
		});
	}

	removeModule { |slot|
		module_array = module_array.removeAt(slot);
	}

	replaceModule { |module, slot|
		module_array.put(slot, module);
	}

	printRackContents {

	}

	sendRack { this.sendSynth } // alias, clearer code like that

	sendSynth {
		var gen;
		if(module_array[0].type == \generator) { gen = 1 } { gen = 0 };
		(module_array.size - gen).do({|i|
			var j = module_array.size - (i+1);
			module_array[j].sendSynth()
		});

		nodeArray_master = nodeArray_master.add(
					Synth(name ++ "_master", [name ++ "_level", level.val, name ++ "_mix",
				mix.val], nodeGroup, 'addToTail'))
	}

	killAllSynths {

	}

	connectToModule {}

	module { |index|
		^module_array[index];
	}


}
