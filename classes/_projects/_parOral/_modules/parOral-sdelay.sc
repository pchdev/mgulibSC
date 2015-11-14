PO_sdelaySTS : MGU_AbstractModule { // faust smooth delay

	// stereo to stereo

	var <dtime_left, <dtime_right;
	var <fbk_left, <fbk_right;
	var <mix;

	*new { |out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters
	}

	initParameters {

		dtime_left = MGU_parameter(container, \dtime_left, Float, [0.01, 2], 0.5, true);
		dtime_right = MGU_parameter(container, \dtime_right, Float, [0.01, 2.0], 0.5, true);
		fbk_left = MGU_parameter(container, \fbk_left, Float, [0, 100], 50, true);
		fbk_right = MGU_parameter(container, \fbk_right, Float, [0, 100], 50, true);
		mix = MGU_parameter(container, \mix, Float, [0, 1], 0.5, true);

		def = SynthDef(name, {
			var inleft, inright, dl_left, dl_right, process;
			inleft = In.ar(inbus.smbKr, 1);
			inright = In.ar(inbus.smbKr + 1, 1);
			dl_left = FaustMguSdelay.ar(inleft, dtime_left.smbKr, fbk_left.smbKr);
			dl_right = FaustMguSdelay.ar(inright, dtime_right.smbKr, fbk_right.smbKr);
			Out.ar(out, [inleft * (1 - mix.smbKr) + (dl_left * mix.smbKr),
				inright * (1 - mix.smbKr) + (dl_right * mix.smbKr)]);
		}).add;


	}

}

PO_sdelayMTS : MGU_AbstractModule {

	// mono to stereo

	var <dtime_left, <dtime_right;
	var <fbk_left, <fbk_right;
	var <mix;

	*new { |out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters
	}

	initParameters {

		dtime_left = MGU_parameter(container, \dtime_left, Float, [0.01, 2], 0.5, true);
		dtime_right = MGU_parameter(container, \dtime_right, Float, [0.01, 2.0], 0.5, true);
		fbk_left = MGU_parameter(container, \fbk_left, Float, [0, 100], 50, true);
		fbk_right = MGU_parameter(container, \fbk_right, Float, [0, 100], 50, true);
		mix = MGU_parameter(container, \mix, Float, [0, 1], 0.5, true);

		def = SynthDef(name, {
			var in, dl_left, dl_right, process;
			in = In.ar(inbus.smbKr, 1);
			dl_left = FaustMguSdelay.ar(in, dtime_left.smbKr, fbk_left.smbKr);
			dl_right = FaustMguSdelay.ar(in, dtime_right.smbKr, fbk_right.smbKr);
			Out.ar(out, [in * (1 - mix.smbKr) + (dl_left * mix.smbKr),
				in * (1 - mix.smbKr) + (dl_right * mix.smbKr)]);
		}).add;

	}

}

PO_sdelayMTM : MGU_AbstractModule {

	// mono to mono

	var <dtime;
	var <fbk;
	var <mix;

	*new { |out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters
	}

	initParameters {

		dtime = MGU_parameter(container, \dtime_left, Float, [0.01, 2], 0.5, true);
		fbk = MGU_parameter(container, \fbk_left, Float, [0, 100], 50, true);
		mix = MGU_parameter(container, \mix, Float, [0, 1], 0.5, true);

		def = SynthDef(name, {
			var in, delay, process;
			in = In.ar(inbus.smbKr, 1);
			delay = FaustMguSdelay.ar(in, dtime.smbKr, fbk.smbKr);
			Out.ar(out, [in * (1 - mix.smbKr) + (delay * mix.smbKr)]);
		}).add;


	}

}