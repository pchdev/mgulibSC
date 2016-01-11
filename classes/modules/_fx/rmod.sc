PO_rmod : MGU_AbstractWavetableModule {

	var <freq;

	*new { |out, server, numInputs = 1, numOutputs = 1, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initParameters {

		freq = MGU_parameter(container, \freq, Float, [0, 20000], 6, true);

		def = SynthDef(name, {
			var in, sin, process, outarray;
			in = In.ar(inbus, numInputs);
			sin = Osc.ar(wavetable.bufnum, freq.kr);
			process = in * sin;
			Out.ar(master_internal, process)
		}).add;
	}
}
	