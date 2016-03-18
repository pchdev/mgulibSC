MGU_cycle : MGU_AbstractWavetableModule { // simple wavetable oscillator module

	var <freq;

	*new { |out = 0, server, numInputs = 1, numOutputs = 1, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\generator)
		.init.initWavetable.initModule.initMasterDef
	}

	initModule {

		freq = MGU_parameter(container, \freq, Float, [0, 44100], 440, true);
		waveform.val = \sine;

		def = SynthDef(name, {
			var gen;
			gen = Osc.ar(wavetable.bufnum, freq.kr, 0);
			Out.ar(master_internal, gen);
		}).add;

	}

}




	