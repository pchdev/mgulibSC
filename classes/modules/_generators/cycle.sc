MGU_cycle : MGU_AbstractWavetableModule {

	var <freq, <gain;

	*new { |out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initWavetable.initParameters
	}

	initParameters {

		freq = MGU_parameter(container, \freq, Float, [0, 44100], 440, true);
		gain = MGU_parameter(container, \gain, Float, [0.0, 10.0], 1.0, true);

		def = SynthDef(name, {
			var gen;
			gen = Osc.ar(wavetable.bufnum, freq.kr, 0, gain.kr);
			Out.ar(out, gen);
		}).add;
	}

}




	