MGU_generator : MGU_AbstractWavetableModule {

	var defaultEnv, ampEnv;
	var freq, amp, phase, morph;
	var gate;
	var graphDesigner, graph;

	*new { |out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initWavetable.initParameters

	}

	initParameters {

		defaultEnv = Env([0, 1, 0], [2, 2], \lin, 1);
		freq = MGU_parameter(container, \freq, Float, [0, 22000], 440, true);
		amp = MGU_parameter(container, \amp, Float, [0, 1], 0.25, true);
		phase = MGU_parameter(container, \phase, Float, [0, 1], 0, true);
		morph = MGU_parameter(container, \morph, Float, [0, inf], 0, true);
		ampEnv = MGU_parameter(container, \ampEnv, Env, nil, defaultEnv, true);
		gate = MGU_parameter(container, \gate, Integer, [0, 1], 1, true);

	}
} 