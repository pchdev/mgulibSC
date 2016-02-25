MGU_AbstractFilterModule : MGU_AbstractModule {

	var <freq, <q, <gain;

	initParameters {

		freq = MGU_parameter(container, \freq, Float, [0, 20000], 2000, true);
		q = MGU_parameter(container, \q, Float, [0, 50], 1, true);
		gain = MGU_parameter(container, \gain, Float, [-96, 12], 0, true, \dB, \amp, 44100);
	}

}

PO_moogVCF : MGU_AbstractFilterModule {

	*new { |out = 0, server, numInputs = 1, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).init.initParameters.initDef
	}

	initDef {

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus.kr, numInputs);
			process = MoogFF.ar(in, freq.kr, gain.kr, 0)
		}).add
	}
}

PO_lpf : MGU_AbstractFilterModule {

	*new { |out = 0, server, numInputs = 1, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).init.initParameters.initDef
	}

	initDef {

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus.kr, numInputs);
			process = BLowPass.ar(in, freq.kr, q.kr, gain.kr);
			Out.ar(out, process);
		}).add
	}
}

PO_hpf : MGU_AbstractFilterModule {

	*new { |out = 0, server, numInputs = 1, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).init.initParameters.initDef
	}

	initDef {

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus.kr, numInputs);
			process = BHiPass.ar(in, freq.kr, q.kr, gain.kr);
			Out.ar(out, process);
		}).add
	}

}

PO_bpf : MGU_AbstractFilterModule {

	*new { |out = 0, server, numInputs = 1, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).init.initParameters.initDef
	}

	initDef {

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus.kr, numInputs);
			process = BBandPass.ar(in, freq.kr, q.kr, gain.kr);
			Out.ar(out, process);
		}).add
	}

}

PO_brf : MGU_AbstractFilterModule {

	*new { |out = 0, server, numInputs = 1, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).init.initParameters.initDef
	}

	initDef {

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus.kr, numInputs);
			process = BBandStop.ar(in, freq.kr, q.kr, gain.kr);
			Out.ar(out, process);
		}).add
	}

}

PO_lsf : MGU_AbstractFilterModule {

	*new { |out = 0, server, numInputs = 1, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).init.initParameters.initDef
	}

	initDef {

		q.outUnit = \dB;

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus.kr, numInputs);
			process = BLowShelf.ar(in, freq.kr, q.kr, gain.kr);
			Out.ar(out, process);
		}).add
	}

}

PO_hsf : MGU_AbstractFilterModule {

	*new { |out = 0, server, numInputs = 1, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).init.initParameters.initDef
	}

	initDef {

		q.outUnit = \dB;

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus.kr, numInputs);
			process = BHiShelf.ar(in, freq.kr, q.kr, gain.kr);
			Out.ar(out, process);
		}).add
	}

}

PO_apf : MGU_AbstractFilterModule {

	*new { |out = 0, server, numInputs = 1, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).init.initParameters.initDef
	}

	initDef {

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus.kr, numInputs);
			process = BAllPass.ar(in, freq.kr, q.kr, gain.kr);
			Out.ar(out, process);
		}).add
	}

}

PO_midEQ : MGU_AbstractFilterModule {

	*new { |out = 0, server, numInputs = 1, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).init.initParameters.initDef
	}

	initDef {

		q.outUnit = \dB;

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus.kr, numInputs);
			process = BPeakEQ.ar.ar(in, freq.kr, q.kr, gain.kr);
			Out.ar(out, process);
		}).add
	}

}

PO_comb : MGU_AbstractFilterModule { // TBC

}

PO_fir : MGU_AbstractFilterModule { // TBC

}


PO_multiEQ : MGU_AbstractModule { // TBC

	var <band;

	*new { |out = 0, server, numInputs = 1, numOutputs = 1, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).init.initBand
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

			band[band.size -2].out = Bus.audio(server, numInputs);
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