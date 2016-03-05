PO_pShifter : MGU_AbstractModule { // pitch-shifting module (doesn't work with faust..)

	var <shift;

	*new { |out = 0, server, numInputs = 1, numOutputs = 1, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		shift = MGU_parameter(container, \shift, Float, [-12.0, 12.0], 0.0, true, \semitones, \ratio);

		def = SynthDef(name, {
			var in, shifter, process;
			in = In.ar(inbus, numInputs);
			shifter = PitchShift.ar(in, 0.2, shift.kr);
			Out.ar(master_internal, shifter);
		}).add;

	}

}


	