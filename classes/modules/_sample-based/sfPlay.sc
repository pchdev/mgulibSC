PO_sfPlayer : MGU_AbstractBufferModule { // simple soundFile player

	var <startPos;
	var <loop, <playstop;

	*new { |out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters;
	}

	initParameters {

		type = \generator;

		loop = MGU_parameter(container, \loop, Integer, [0, 1], 1, true);
		playstop = MGU_parameter(container, \playStop, Symbol, nil, \stop, true);
		startPos = MGU_parameter(container, \startPos, Integer, [0, inf], 0, true, \ms, \samps);
		playstop.parentAccess = this;

	}

	paramCallBack { |param, value|
		switch(param,
			\playStop, { switch(value[0],
				\play, { this.sendSynth },
				\stop, { this.killSynths })};
		);
	}

	bufferLoaded { // separated from this.initParameters, must read soundFile first

		startPos.sr = sampleRate; //
		def = SynthDef(name, {
			var bufrd;
			bufrd = PlayBuf.ar(numChannels, buffer.bufnum, 1, 1, startPos.kr, loop.kr, 2);
			Out.ar(master_internal, bufrd);
		}).add;

	}
}

		