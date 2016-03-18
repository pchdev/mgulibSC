MGU_joshVocoder : MGU_AbstractWavetableModule {

	var <freq;

	*new { |out = 0, server, numInputs = 1, numOutputs = 1, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\effect)
		.init.initWavetable.initModule.initMasterDef
	}

	initModule {

		freq = MGU_parameter(container, \freq, Float, [1, 20000], 440, true);

		def = SynthDef(name, {
			var in, car, process;
			in = In.ar(inbus, numInputs);
			car = WhiteNoise.ar();
			process = Vocoder.ar(car, in, 24, 100, 5000, 0.02, 3000, 0.05, 25);
			Out.ar(master_internal, process)
		}).add;

	}
}

MGU_joshVocoder2 : MGU_AbstractModule {

	*new { |out = 0, server, numInputs = 1, numOutputs = 1, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		def = SynthDef(name, {
			var in, car, process;
			in = In.ar(inbus, numInputs);
			car = PinkNoise.ar();
			process = Vocoder.ar(car, in, 12, 100, 5000, 0.02, 3000, 0.05, 25);
			Out.ar(master_internal, process)
		}).add;

	}
}
