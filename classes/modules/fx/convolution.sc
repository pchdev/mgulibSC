MGU_convolver : MGU_AbstractBufferModule {

	var <fftsize;
	var irspectrum, bufsize;

	*new { |out, server, name|
		^super.newCopyArgs(out, server, name).init.initParameters;
	}

	initParameters {

		fftsize = MGU_parameter(container, \fftsize, Integer, [2048, 8192], 2048, true);

	}

	sendDef { // method called whenever a file is loaded into the buffer

		bufsize = PartConv.calcBufSize(fftsize.val, buffer);
		irspectrum = Buffer.alloc(server, bufsize, 1);
		irspectrum.preparePartConv(buffer, fftsize.val);

		buffer.free;

		def = SynthDef(name, {
			var conv_l, conv_r;
			conv_l = PartConv.ar(In.ar(inbus.kr), fftsize.kr, irspectrum.bufnum);
			conv_r = PartConv.ar(In.ar(inbus.kr + 1), fftsize.kr, irspectrum.bufnum);
			Out.ar(out, [conv_l, conv_r]);
		}).add;

	}

	readIR { |path| // alias for readFile method
		this.readFile(path);
	}

}
		