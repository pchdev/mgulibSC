PO_fShifter : MGU_AbstractModule { // frequency shifter module -- TBC

	var <freq;

	*new { |out = 0, server, numInputs = 1, numOutputs = 1, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		freq = MGU_parameter(container, \freq, Float, [-20000, 20000], 50, true);

		def = SynthDef(name, {
			var in, shift;
			in = In.ar(inbus, numInputs);
			shift = FreqShift.ar(in, freq.kr);
			Out.ar(master_internal, shift);
		}).add;

	}

}

	