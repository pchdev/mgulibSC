MGU_sineLFO : MGU_AbstractModule {
	var <freq, <depth;

	*new { |out = 0, server, num_inputs = 0, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs).type_(\control)
		.init.initModule
	}

	initModule {

		freq = MGU_parameter(container, \freq, Float, [0, 20], 1, true);
		depth = MGU_parameter(container, \depth, Float, [0, 1], 1, true);

		def = SynthDef(name, {
			var process;
			process = SinOsc.kr(freq.kr, 0, depth.kr);
			Out.kr(out, process)
		}).add;
	}
}