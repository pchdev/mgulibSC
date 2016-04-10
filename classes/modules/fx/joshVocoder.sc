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
			car = PinkNoise.ar();
			process = Vocoder.ar(car, in, 24, 100, 5000, 0.02, 3000, 0.05, 25);
			Out.ar(master_internal, process)
		}).add;

	}
}

MGU_joshVocoder2 : MGU_AbstractModule {

	var <harm, <anabwscale, <outbw, <freqmul;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		harm = MGU_parameter(container, \harm, Float, [0, 10], 0.5, true);
		anabwscale = MGU_parameter(container, \anabwscale, Float, [0, 1], 0.5, true);
		outbw = MGU_parameter(container, \outbw, Float, [0.01, 2], 0.01, true);
		freqmul = MGU_parameter(container, \freqmul, Float, [0.5, 4], 1, true);

		def = SynthDef(name, {
			var in, car, process, pan;
			in = In.ar(inbus, num_inputs);
			car = PinkNoise.ar();
			process = Vocode.ar(car, harm.kr, in, anabwscale.kr, outbw.kr, freqmul.kr);
			pan = Pan2.ar(process, 1);
			Out.ar(master_internal, process)
		}).add;



	}
}
