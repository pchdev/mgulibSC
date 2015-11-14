PO_rmod : MGU_AbstractModule {

	var <freq, <mix;

	*new { |out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters
	}

	initParameters {

		name ?? { name = "rmod_" ++ thisInstance };
		freq = MGU_parameter(container, \freq, Float, [0, 20000], 6, true);
		mix = MGU_parameter(container, \mix, Float, [0, 1], 1, true);

		def = SynthDef(name, {
			var in, sin, process, outarray;
			in = In.ar(inbus.smbKr);
			sin = SinOsc.ar(freq.smbKr);
			process = in * sin;
			outarray = Mix.ar([in * (1 - mix.smbKr), process * mix.smbKr]);
			Out.ar(out, outarray)
		}).add;

	}
}
	