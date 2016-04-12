MGU_ParOralTrigger : MGU_AbstractModule {

	var <lastIndex;

	*new { |out = 0, server, num_inputs, num_outputs, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\control)
		.init.initModule;
	}

	initModule {
		lastIndex = MGU_parameter(container, \lastIndex, Integer, [0, 200000], 0, true);
	}

}