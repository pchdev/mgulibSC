MGU_moduleRack : MGU_AbstractModule {

	var <module_array;

	*new { |out = 0, server, num_inputs = 2, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initRack.initMasterDef;
	}

	initRack {
		module_array = [];
	}

	addModule { |module|
		module_array = module_array.add(module);
		container.registerContainer(module_array[module_array.size -1].container);

		if((module_array.size == 1) && (module_array[0].type == \effect)) {
			// rack input = first effect input
			this.inbus = module_array[0].inbus;
		};

		if(module_array.size > 1) {
			module_array[module_array.size -2].connectToModule(module_array[module_array.size -1]);
		};

		module_array[module_array.size -1].out = master_internal.index;

	}

	addModules { |...array|
		array.size.do({|i|
			this.addModule(array[i]);
		});
	}

	removeModule { |slot| // tbi
		module_array = module_array.removeAt(slot);
	}

	replaceModule { |module, slot| // tbi
		module_array.put(slot, module);
	}

	printRackContents { // tbi

	}

	sendRack { this.sendSynth } // alias, clearer code like that

	sendSynth {
		var gen;
		if(module_array[0].type == \generator) { gen = 1 } { gen = 0 };
		(module_array.size - gen).do({|i|
			var j = module_array.size - (i+1);
			module_array[j].sendSynth()
		});

		node_array_master = node_array_master.add(
			Synth(name ++ "_master", master_container.makeSynthArray.asOSCArgArray,
				node_group, 'addToTail'));

		// create sends
		send_array !? {
			send_array.size.do({|i|
				node_array_send = node_array_send.add(
					Synth(name ++ "_send" ++ (i+1),
						sends_container.makeSynthArray.asOSCArgArray, node_group, 'addToTail'));
			})
		};
	}

	killAllSynths { // tbi

	}

	connectToModule {} // is this useful ?

	module { |index|
		^module_array[index];
	}


}
