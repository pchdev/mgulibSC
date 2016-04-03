MGU_AbstractFilterModule : MGU_AbstractModule {

	var <freq, <q, <gain;

	initModule {

		freq = MGU_parameter(container, \freq, Float, [0, 20000], 2000, true);
		q = MGU_parameter(container, \q, Float, [0, 50], 1, true);
		gain = MGU_parameter(container, \gain, Float, [-96, 12], 0, true, \dB, \amp, 44100);
	}

}

PO_moogVCF : MGU_AbstractFilterModule {

	*new { |out = 0, server, num_inputs = 1, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).init.initParameters.initDef
	}

	initDef {

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus, num_inputs);
			process = MoogFF.ar(in, freq.kr, gain.kr, 0)
		}).add
	}
}

PO_lpf : MGU_AbstractFilterModule {

	*new { |out = 0, server, num_inputs = 1, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initDef.initMasterDef
	}

	initDef {

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus, num_inputs);
			process = BLowPass.ar(in, freq.kr, q.kr, gain.kr);
			Out.ar(master_internal, process);
		}).add
	}
}

PO_hpf : MGU_AbstractFilterModule {

	*new { |out = 0, server, num_inputs = 1, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).init.initParameters.initDef
	}

	initDef {

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus, num_inputs);
			process = BHiPass.ar(in, freq.kr, q.kr, gain.kr);
			Out.ar(master_internal, process);
		}).add
	}

}

PO_bpf : MGU_AbstractFilterModule {

	*new { |out = 0, server, num_inputs = 1, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).init.initParameters.initDef
	}

	initDef {

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus, num_inputs);
			process = BBandPass.ar(in, freq.kr, q.kr, gain.kr);
			Out.ar(master_internal, process);
		}).add
	}

}

PO_brf : MGU_AbstractFilterModule {

	*new { |out = 0, server, num_inputs = 1, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).init.initParameters.initDef
	}

	initDef {

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus, num_inputs);
			process = BBandStop.ar(in, freq.kr, q.kr, gain.kr);
			Out.ar(master_internal, process);
		}).add
	}

}

PO_lsf : MGU_AbstractFilterModule {

	*new { |out = 0, server, num_inputs = 1, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).init.initParameters.initDef
	}

	initDef {

		q.outUnit = \dB;

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus, num_inputs);
			process = BLowShelf.ar(in, freq.kr, q.kr, gain.kr);
			Out.ar(master_internal, process);
		}).add
	}

}

PO_hsf : MGU_AbstractFilterModule {

	*new { |out = 0, server, num_inputs = 1, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).init.initParameters.initDef
	}

	initDef {

		q.outUnit = \dB;

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus, num_inputs);
			process = BHiShelf.ar(in, freq.kr, q.kr, gain.kr);
			Out.ar(master_internal, process);
		}).add
	}

}

PO_apf : MGU_AbstractFilterModule {

	*new { |out = 0, server, num_inputs = 1, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).init.initParameters.initDef
	}

	initDef {

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus, num_inputs);
			process = BAllPass.ar(in, freq.kr, q.kr, gain.kr);
			Out.ar(master_internal, process);
		}).add
	}

}

PO_midEQ : MGU_AbstractFilterModule {

	*new { |out = 0, server, num_inputs = 1, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).init.initParameters.initDef
	}

	initDef {

		q.outUnit = \dB;

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus, num_inputs);
			process = BPeakEQ.ar.ar(in, freq.kr, q.kr, gain.kr);
			Out.ar(master_internal, process);
		}).add
	}

}

PO_comb : MGU_AbstractFilterModule { // TBC

}

PO_fir : MGU_AbstractFilterModule { // TBC

}


PO_multiEQ : MGU_AbstractModule { // TBC

	var <band;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).init.initBand
	}


	initBand {

		band = [];
	}

	addBand { |module, freqArg|

		module.out = out;
		freqArg !? { module.freq.val = freqArg };
		band = band.add(module);
		container.registerContainer(band[band.size -1].container);

		if(band.size > 1) {

			// if more than one band, create a new output bus for the
			// previous band and connect the next one

			band[band.size -2].out = Bus.audio(server, num_inputs);
			band[band.size -2].connectToModule(band[band.size -1]);

		};

	}

	removeBand {|index|

	}

	replaceBand { |index, module|

	}

	connectToModule { |module| // overriding abstract method
		if(out.class == Bus, { module.inbus.val = out.index }, {
			module.inbus.val = out });
		band[0].inbus.val = inbus.val;
	}


}