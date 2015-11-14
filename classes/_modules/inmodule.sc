MGU_inModule : MGU_AbstractModule {

	var <gain;

	*new { |out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters
	}

	initParameters {

		gain = MGU_parameter(container, \gain, Float, [-96, 12], 0, true, \dB, \amp);

		def = SynthDef(name, {
			var in = SoundIn.ar(inbus.smbKr, gain.smbKr);
			Out.ar(out, in);
		}).add

	}

}