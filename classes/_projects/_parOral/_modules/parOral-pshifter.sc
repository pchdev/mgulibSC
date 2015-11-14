PO_pShifter : MGU_AbstractModule { // pitch-shifting module (doesn't work with faust..)

	var <shift, <mix;

	*new { |out, server, numChannels = 1, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters
	}

	initParameters {

		mix = MGU_parameter(container, \mix, Float, [0.0, 1.0], 1.0, true);
		shift = MGU_parameter(container, \shift, Float, [-12.0, 12.0], 0.3, true);

		def = SynthDef(name, {
			var in, shifter, process;
			inbus.val.postln;
			in = In.ar(inbus.smbKr);
			shifter = PitchShift.ar(in, 0.2, shift.smbKr);
			process = Mix.ar([in * (1 - mix.smbKr), shifter * mix.smbKr]);
			Out.ar(out, process);
		}).add;

	}

}


	