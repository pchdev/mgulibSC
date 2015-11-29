PO_sfPlayer : MGU_AbstractBufferModule { // simple soundFile player

	var <gain, <startPos;
	var <loop, <playstop;

	*new { |out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters;
	}

	initParameters {

		loop = MGU_parameter(container, \loop, Integer, [0, 1], 1, true);
		playstop = MGU_parameter(container, \startstop, Symbol, nil, \stop, true);
		gain = MGU_parameter(container, \gain, Float, [0.0, 4.0], 1.0, true);
		startPos = MGU_parameter(container, \startPos, Integer, [0, inf], 0, true, \ms, \samps);
		playstop.parentAccess = this;

	}

	paramCallBack { |param, value|
		switch(param,
			\startstop, { switch(value[0],
				\start, { this.sendSynth },
				\stop, { this.killSynths })};
		);
	}

	bufferLoaded { // separated from this.initParameters, must read soundFile first
		startPos.sr = sampleRate; //
		def = SynthDef(name, {
			var bufrd;
			bufrd = PlayBuf.ar(numChannels, buffer.bufnum, 1, 1, startPos.kr, loop.kr, 2);
			Out.ar(out, bufrd * gain.kr);
		}).add;

	}


}

		