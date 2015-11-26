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
			inleft = In.ar(inbus.kr, 1);
			inright = In.ar(inbus.kr + 1, 1);
			dl_left = FaustMguSdelay.ar(inleft, dtime_left.kr, fbk_left.kr);
			dl_right = FaustMguSdelay.ar(inright, dtime_right.kr, fbk_right.kr);
			Out.ar(out, [FaustDrywet.ar(inleft, dl_left, mix.kr), FaustDrywet.ar(inright, dl_right, mix.kr)]);
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
			dl_left = FaustMguSdelay.ar(in, dtime_left.kr, fbk_left.kr);
			dl_right = FaustMguSdelay.ar(in, dtime_right.kr, fbk_right.kr);
			Out.ar(out, [FaustDrywet.ar(in, dl_left, mix.kr), FaustDrywet.ar(in, dl_right, mix.kr)]);
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
			in = In.ar(inbus.kr, 1);
			delay = FaustMguSdelay.ar(in, dtime.kr, fbk.kr);
			Out.ar(out, FaustDrywet.ar(in, delay, mix.kr));
		}).add;


	}

}