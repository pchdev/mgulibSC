PO_sdelaySTS : MGU_AbstractModule { // faust smooth delay

	// stereo to stereo

	var <dtime_left, <dtime_right;
	var <fbk_left, <fbk_right;

	*new { |out = 0, server, num_inputs = 2, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		dtime_left = MGU_parameter(container, \dtime_left, Float, [0.01, 2], 0.5, true);
		dtime_right = MGU_parameter(container, \dtime_right, Float, [0.01, 2.0], 0.5, true);
		fbk_left = MGU_parameter(container, \fbk_left, Float, [0, 100], 50, true);
		fbk_right = MGU_parameter(container, \fbk_right, Float, [0, 100], 50, true);

		def = SynthDef(name, {
			var in, dl_left, dl_right, process;
			in = In.ar(inbus, 2);
			dl_left = FaustMguSdelay.ar(in[0], dtime_left.kr, fbk_left.kr);
			dl_right = FaustMguSdelay.ar(in[1], dtime_right.kr, fbk_right.kr);
			process = [dl_left, dl_right];
			Out.ar(master_internal, process);
		}).add;
	}

}

PO_sdelayMTS : MGU_AbstractModule {

	// mono to stereo

	var <dtime_left, <dtime_right;
	var <fbk_left, <fbk_right;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		dtime_left = MGU_parameter(container, \dtime_left, Float, [0.01, 2], 0.5, true);
		dtime_right = MGU_parameter(container, \dtime_right, Float, [0.01, 2.0], 0.5, true);
		fbk_left = MGU_parameter(container, \fbk_left, Float, [0, 100], 50, true);
		fbk_right = MGU_parameter(container, \fbk_right, Float, [0, 100], 50, true);

		def = SynthDef(name, {
			var in, dl_left, dl_right, process;
			in = In.ar(inbus, 1);
			dl_left = FaustMguSdelay.ar(in, dtime_left.kr, fbk_left.kr);
			dl_right = FaustMguSdelay.ar(in, dtime_right.kr, fbk_right.kr);
			process = [dl_left, dl_right];
			Out.ar(master_internal, process);
		}).add;

	}

}

PO_sdelayMTM : MGU_AbstractModule {

	// mono to mono

	var <dtime;
	var <fbk;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		dtime = MGU_parameter(container, \dtime_left, Float, [0.01, 2], 0.5, true);
		fbk = MGU_parameter(container, \fbk_left, Float, [0, 100], 50, true);

		def = SynthDef(name, {
			var in, delay;
			in = In.ar(inbus, 1);
			delay = FaustMguSdelay.ar(in, dtime.kr, fbk.kr);
			Out.ar(master_internal, delay);
		}).add;


	}

}