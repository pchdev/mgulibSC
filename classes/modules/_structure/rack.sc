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
	}

	removeModule { |slot|
		module_array = module_array.removeAt(slot);
	}

	replaceModule { |module, slot|
		module_array.put(slot, module);
	}

	printRackContents {

	}


}
