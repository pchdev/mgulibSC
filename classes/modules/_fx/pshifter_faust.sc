PO_pshifter2 : MGU_AbstractModule {

	var <shift, <window, <xfade;

	*new { |out = 0, server, numInputs = 1, numOutputs = 1, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		shift = MGU_parameter(container, \shift, Float, [-12, 12], 0, true);
		xfade = MGU_parameter(container, \xfade, Integer, [1, 10000], 10, true);
		window = MGU_parameter(container, \window, Integer, [50, 10000], 1000, true);

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus, numInputs);
			process = FaustPitchShifter.ar(in, shift.kr, window.kr, xfade.kr);
			Out.ar(master_internal, process);
		}).add;


	}

}