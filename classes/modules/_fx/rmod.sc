PO_rmod : MGU_AbstractWavetableModule {

	var <freq, <mix;

	*new { |out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters
	}

	initParameters {

		freq = MGU_parameter(container, \freq, Float, [0, 20000], 6, true);
		mix = MGU_parameter(container, \mix, Float, [0, 1], 1, true);

		def = SynthDef(name, {
			var in, sin, process, outarray;
			in = In.ar(inbus.kr, numChannels);
			sin = Osc.ar(wavetable.bufnum, freq.kr);
			process = in * sin;
			outarray = FaustDrywet.ar(in, process, mix.kr);
			Out.ar(out, outarray)
		}).add;
	}
}
	