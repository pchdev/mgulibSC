PO_fShifter : MGU_AbstractModule { // frequency shifter module -- TBC

	var <freq;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		freq = MGU_parameter(container, \freq, Float, [-20000, 20000], 50, true);

		def = SynthDef(name, {
			var in, shift;
			in = In.ar(inbus, num_inputs);
			shift = FreqShift.ar(in, freq.kr);
			Out.ar(master_internal, shift);
		}).add;

	}

}

	