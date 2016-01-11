PO_granaryMTS : MGU_AbstractModule { // granular delay, mono to stereo -> ParOral

	var buffer_length;
	var buffer;
	var numFrames;

	*new { |out = 0, server, numInputs = 1, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		buffer_length = 1; // sec
		numFrames = 44100 * buffer_length;
		buffer = Buffer.alloc(server, numFrames, numInputs);

		def = SynthDef(name, {
			var in = In.ar(inbus, numInputs);
			var phasor = Phasor.ar(0, 1, 0, 1);
			var bufrd = BufRd.ar(numOutputs, buffer.bufnum, phasor * numFrames, 1, 2);
		});

	}

}

	