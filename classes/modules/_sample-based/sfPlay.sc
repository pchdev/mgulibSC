PO_sfPlayer : MGU_AbstractBufferModule { // simple soundFile player

	var <startPos;
	var <loop, <playstop;

	*new { |out = 0, server, numInputs = 0, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\generator)
		.init.initModule.initMasterDef;
	}

	initModule {

		loop = MGU_parameter(container, \loop, Integer, [0, 1], 1, true);
		playstop = MGU_parameter(container, \playStop, Symbol, nil, \stop, true);
		startPos = MGU_parameter(container, \startPos, Integer, [0, inf], 0, true, \ms, \samps);
		playstop.parentAccess = this; // allows access to this for parameter call back;

	}

	paramCallBack { |param, value|
		switch(param,
			\playStop, { switch(value[0],
				\play, { this.sendSynth },
				\stop, { this.killAllSynths })};
		);
	}

	bufferLoaded {
		startPos.range[1] = (numFrames / sampleRate) * 1000;
		startPos.sr = sampleRate; //
		def = SynthDef(name, {
			var bufrd;
			bufrd = PlayBuf.ar(numOutputs, buffer.bufnum, 1, 1, startPos.kr, loop.kr, 2);
			Out.ar(master_internal, bufrd);
		}).add;

	}
}

		