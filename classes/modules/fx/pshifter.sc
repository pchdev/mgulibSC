PO_pShifter : MGU_AbstractModule { // pitch-shifting module (doesn't work with faust..)

	var <shift;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		shift = MGU_parameter(container, \shift, Float, [-12.0, 12.0], 0.0, true, \semitones, \ratio);

		def = SynthDef(name, {
			var in, shifter, process;
			in = In.ar(inbus, num_inputs);
			shifter = PitchShift.ar(in, 0.2, shift.kr);
			Out.ar(master_internal, shifter);
		}).add;

	}

}

PO_pshifter2 : MGU_AbstractModule {

	var <shift, <window, <xfade;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		shift = MGU_parameter(container, \shift, Float, [-12, 12], 0, true);
		xfade = MGU_parameter(container, \xfade, Integer, [1, 10000], 10, true);
		window = MGU_parameter(container, \window, Integer, [50, 10000], 1000, true);

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus, num_inputs);
			process = FaustPitchShifter.ar(in, shift.kr, window.kr, xfade.kr);
			Out.ar(master_internal, process);
		}).add;


	}

}