MGU_moduleRack : MGU_AbstractModule {

	var <module_array;

	*new { |out = 0, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters
	}

	initParameters {
		module_array = [];
	}

	addModule { |module|
		module_array = module_array.add(module);
		container.registerContainer(module_array[module_array.size -1].container);
		if(module_array.size > 1) {
			module_array[module_array.size -1].out = out;
			(module_array.size -1).do({|i|
				module_array[i].out = nil;
			});
			module_array[module_array.size -2].connectToModule(module_array[module_array.size -1]);
		};
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

	sendSynth {
		(module_array.size - 1).do({|i|
			var j = module_array.size - (i+1);
			module_array[j].sendSynth()
		});
	}

	killSynths {

	}

	module { |index|
		^module_array[index];
	}


}
