PO_rmod : MGU_AbstractWavetableModule {

	var <freq;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initWavetable.initModule.initMasterDef
	}

	initModule {

		freq = MGU_parameter(container, \freq, Float, [0, 20000], 6, true);
		waveform.val = \sine;

		def = SynthDef(name, {
			var in, sin, process, outarray;
			in = In.ar(inbus, num_inputs);
			sin = Osc.ar(wavetable.bufnum, freq.kr);
			process = in * sin;
			Out.ar(master_internal, process)
		}).add;
	}
}
	