MGU_limiter : MGU_AbstractModule {

	var <ceil, <dur;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		description = "based on SC Limiter UGen...";

		ceil = MGU_parameter(container, \ceil, Float, [-96, 0], -0.3, true, \dB, \amp);
		dur = MGU_parameter(container, \dur, Integer, [5, 100], 10, true, \ms, \s);

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus, num_inputs);
			process = Limiter.ar(in, ceil.kr, dur.kr);
			Out.ar(master_internal, process);
		}).add;

	}

}