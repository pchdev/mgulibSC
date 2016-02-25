PO_grip : MGU_AbstractModule { // buffer freeze -- TBC

	*new {|out = 0, server, numInputs = 1, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

	}

}