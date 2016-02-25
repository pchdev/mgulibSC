PO_sdelaySTS : MGU_AbstractModule { // faust smooth delay

	// stereo to stereo

	var <dtime_left, <dtime_right;
	var <fbk_left, <fbk_right;

	*new { |out = 0, server, numInputs = 2, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type(\effect)
		.init.initModule.initMasterDef
	}

	initParameters {

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

	*new { |out = 0, server, numInputs = 1, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initParameters {

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

	*new { |out = 0, server, numInputs = 1, numOutputs = 1, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initParameters {

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