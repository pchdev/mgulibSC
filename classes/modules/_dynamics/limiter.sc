MGU_limiter : MGU_AbstractModule {

	var <ceil, <dur;

	*new { |out = 0, server, numInputs = 1, numOutputs = 1, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		ceil = MGU_parameter(container, \ceil, Float, [-96, 0], -0.3, true, \dB, \amp);
		dur = MGU_parameter(container, \dur, Integer, [5, 100], 10, true, \ms, \s);

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus, numInputs);
			process = Limiter.ar(in, ceil.kr, dur.kr);
			Out.ar(master_internal, process);
		}).add;

	}

}