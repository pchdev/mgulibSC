PO_sfPlayer : MGU_AbstractBufferModule { // simple soundFile player

	var <startPos;
	var <loop, <playstop;
	var <pause;

	*new { |out = 0, server, num_inputs = 0, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\generator)
		.init.initModule.initMasterDef;
	}

	initModule {

		description = "simple soundfile player...";

		startPos = MGU_parameter(container, \startPos, Integer, [0, inf], 0, true, \ms, \samps);
		loop = MGU_parameter(container, \loop, Integer, [0, 1], 1, true);
		pause = MGU_parameter(container, \pause, Integer, [0, 1], 0, true);

	}

	bufferLoaded {
		startPos.range[1] = (num_frames / samplerate) * 1000;
		startPos.sr = samplerate; //
		def = SynthDef(name, {
			var imp, phasor, bufrd;
			imp = Impulse.ar((BufFrames.kr(buffer.bufnum)/SampleRate.ir).reciprocal);
			phasor = Phasor.ar(imp, (1 - pause.kr), startPos.kr, BufFrames.kr(buffer.bufnum), 0);
				bufrd = BufRd.ar(num_outputs, buffer.bufnum, phasor, loop.kr) * (1 - pause.kr);
			Out.ar(master_internal, bufrd);
		}).add;

	}
}


		