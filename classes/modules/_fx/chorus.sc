PO_chorusMTS : MGU_AbstractModule {

	// mono to stereo

	var <dtime_left, <dtime_right;
	var <fbk_left, <fbk_right;
	var <freq;
	var <depth, <mix;

	*new { |out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters
	}

	initParameters {

		dtime_left = MGU_parameter(container, \dtime_left, Float, [0.001, 0.1], 0.2, true);
		dtime_right = MGU_parameter(container, \dtime_right, Float, [0.001, 0.1], 0.2, true);
		fbk_left = MGU_parameter(container, \fbk_left, Float, [0, 100], 50, true);
		fbk_right = MGU_parameter(container, \fbk_right, Float, [0, 100], 50, true);
		freq = MGU_parameter(container, \freq, Float, [0, 100], 0.2, true);
		depth = MGU_parameter(container, \depth, Float, [0, 100], 50, true);
		mix = MGU_parameter(container, \drywet, Float, [0.0, 1.0], 0.5, true);

		def = SynthDef(name, {
			var chorusL, chorusR, process, in;
			in = In.ar(inbus.kr);
			chorusL = ChoruserSC.ar(in, freq.kr, depth.kr,
				dtime_left.kr, fbk_left.kr);
			chorusR = ChoruserSC.ar(in, freq.kr, depth.kr,
				dtime_right.kr, fbk_right.kr);
			process = [FaustDrywet.ar(in, chorusL, mix.kr), FaustDrywet.ar(in, chorusR, mix.kr)];
			Out.ar(out, process);
		}).add;
	}

}

PO_chorusMTM : MGU_AbstractModule {

	var <dtime, <fbk, <freq, <depth, <mix;

	*new { |out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters
	}

	initParameters {

		dtime = MGU_parameter(container, \dtime, Float, [0.001, 0.1], 0.02, true);
		fbk = MGU_parameter(container, \fbk, Float, [0, 100], 50, true);
		freq = MGU_parameter(container, \freq, Float, [0, 100], 0.2, true);
		depth = MGU_parameter(container, \depth, Float, [0, 100], 50, true);
		mix = MGU_parameter(container, \mix, Float, [0.0, 1.0], 0.5, true);

		def = SynthDef(name, {
			var in, chorus, process;
			in = In.ar(inbus.kr);
			chorus = ChoruserSC.ar(in, freq.kr, depth.kr, dtime.kr, fbk.kr);
			process = FaustDrywet.ar(in, chorus, mix.kr);
			Out.ar(out, process);
		}).add;
	}

}

PO_chorusSTS : MGU_AbstractModule {

	var dtime_left, dtime_right, fbk_left, fbk_right, freq, depth, mix;

	*new { |out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters
	}

	initParameters {

		dtime_left = MGU_parameter(container, \dtime_left, Float, [0.001, 0.1], 0.2, true);
		dtime_right = MGU_parameter(container, \dtime_right, Float, [0.001, 0.1], 0.2, true);
		fbk_left = MGU_parameter(container, \fbk_left, Float, [0, 100], 50, true);
		fbk_right = MGU_parameter(container, \fbk_right, Float, [0, 100], 50, true);
		freq = MGU_parameter(container, \freq, Float, [0, 100], 0.2, true);
		depth = MGU_parameter(container, \depth, Float, [0, 100], 50, true);
		mix = MGU_parameter(container, \drywet, Float, [0.0, 1.0], 0.5, true);

		def = SynthDef(name, {
			var in_l, in_r, chorus_l, chorus_r, process;
			in_l = In.ar(inbus.kr);
			in_r = In.ar(inbus.kr + 1);
			chorus_l = ChoruserSC.ar(in_l, freq.kr, depth.kr, dtime_left.kr, fbk_left.kr);
			chorus_r = ChoruserSC.ar(in_r, freq.kr, depth.kr, dtime_right.kr, dtime_right.kr);
			process = [FaustDrywet.ar(in_l, chorus_l, mix.kr), FaustDrywet.ar(in_r, chorus_r, mix.kr)];
			Out.ar(out, process);
		}).add;
	}

}