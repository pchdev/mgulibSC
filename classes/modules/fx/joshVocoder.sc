MGU_joshVocoder : MGU_AbstractWavetableModule {

	var <freq;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initWavetable.initModule.initMasterDef
	}

	initModule {

		freq = MGU_parameter(container, \freq, Float, [1, 20000], 440, true);

		def = SynthDef(name, {
			var in, car, process;
			in = In.ar(inbus, num_inputs);
			car = WhiteNoise.ar();
			process = Vocoder.ar(car, in, 24, 100, 5000, 0.02, 3000, 0.05, 25);
			Out.ar(master_internal, process)
		}).add;

	}
}

MGU_joshVocoder2 : MGU_AbstractModule {

	*new { |out = 0, server, num_inputs = 1, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		def = SynthDef(name, {
			var in, car, process;
			in = In.ar(inbus, num_inputs);
			car = PinkNoise.ar();
			process = Vocoder.ar(car, in, 12, 100, 5000, 0.02, 3000, 0.05, 25);
			Out.ar(master_internal, process)
		}).add;

	}
}
