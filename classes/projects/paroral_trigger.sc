MGU_ParOralTrigger : MGU_AbstractModule {

	var <lastIndex, <scene;

	*new { |out = 0, server, num_inputs = 0, num_outputs = 0, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\nothing)
		.init.initModule;
	}

	initModule {
		lastIndex = MGU_parameter(container, \lastIndex, Integer, [0, 200000], 0, true);
		scene = MGU_parameter(container, \scene, Integer, [1, 4], 1, true);
	}

	sendSynth {}



}