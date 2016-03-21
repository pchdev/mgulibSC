PO_chorusMTS : MGU_AbstractModule {

	// mono to stereo

	var <dtime_left, <dtime_right;
	var <fbk_left, <fbk_right;
	var <freq;
	var <depth;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef;
	}

	initModule {

		dtime_left = MGU_parameter(container, \dtime_left, Float, [0.001, 0.1], 0.2, true);
		dtime_right = MGU_parameter(container, \dtime_right, Float, [0.001, 0.1], 0.2, true);
		fbk_left = MGU_parameter(container, \fbk_left, Float, [0, 100], 50, true);
		fbk_right = MGU_parameter(container, \fbk_right, Float, [0, 100], 50, true);
		freq = MGU_parameter(container, \freq, Float, [0, 100], 0.2, true);
		depth = MGU_parameter(container, \depth, Float, [0, 100], 50, true);

		def = SynthDef(name, {
			var chorusL, chorusR, process, in;
			in = In.ar(inbus, 1);
			chorusL = ChoruserSC.ar(in, freq.kr, depth.kr,
				dtime_left.kr, fbk_left.kr);
			chorusR = ChoruserSC.ar(in, freq.kr, depth.kr,
				dtime_right.kr, fbk_right.kr);
			process = [chorusL, chorusR];
			Out.ar(master_internal, process);
		}).add;
	}

}

PO_chorusMTM : MGU_AbstractModule {

	var <dtime, <fbk, <freq, <depth;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef;
	}

	initModule {

		dtime = MGU_parameter(container, \dtime, Float, [0.001, 0.1], 0.02, true);
		fbk = MGU_parameter(container, \fbk, Float, [0, 100], 50, true);
		freq = MGU_parameter(container, \freq, Float, [0, 100], 0.2, true);
		depth = MGU_parameter(container, \depth, Float, [0, 100], 50, true);

		def = SynthDef(name, {
			var in, chorus, process;
			in = In.ar(inbus, 1);
			chorus = ChoruserSC.ar(in, freq.kr, depth.kr, dtime.kr, fbk.kr);
			process = chorus;
			Out.ar(master_internal, process);
		}).add;
	}

}

PO_chorusSTS : MGU_AbstractModule {

	var <dtime_left, <dtime_right, <fbk_left, <fbk_right, <freq, <depth;

	*new { |out = 0, server, num_inputs = 2, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef;
	}

	initModule {

		dtime_left = MGU_parameter(container, \dtime_left, Float, [0.001, 0.1], 0.2, true);
		dtime_right = MGU_parameter(container, \dtime_right, Float, [0.001, 0.1], 0.2, true);
		fbk_left = MGU_parameter(container, \fbk_left, Float, [0, 100], 50, true);
		fbk_right = MGU_parameter(container, \fbk_right, Float, [0, 100], 50, true);
		freq = MGU_parameter(container, \freq, Float, [0, 100], 0.2, true);
		depth = MGU_parameter(container, \depth, Float, [0, 100], 50, true);

		def = SynthDef(name, {
			var in, chorus_l, chorus_r, process;
			in = In.ar(inbus, 2);
			chorus_l = ChoruserSC.ar(in[0], freq.kr, depth.kr, dtime_left.kr, fbk_left.kr);
			chorus_r = ChoruserSC.ar(in[1], freq.kr, depth.kr, dtime_right.kr, dtime_right.kr);
			process = [chorus_l, chorus_r];
			Out.ar(master_internal, process);
		}).add;
	}

}