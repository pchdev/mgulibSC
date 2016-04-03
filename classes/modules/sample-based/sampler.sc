PO_sampler : MGU_AbstractBufferModule { // simple soundFile player

	var <startPos, <length;
	var <loop, <playstop;
	var <pitch;
	var <attack, <decay, <sustain, <release;
	var <env;

	*new { |out = 0, server, num_inputs = 0, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\generator)
		.init.initModule.initMasterDef;
	}

	initModule {

		loop = MGU_parameter(container, \loop, Integer, [0, 1], 1, true);
		playstop = MGU_parameter(container, \playStop, Symbol, nil, \stop, true);
		startPos = MGU_parameter(container, \startPos, Integer, [0, inf], 0, true, \ms, \samps);
		length = MGU_parameter(container, \length, Integer, [1, inf], 1000, true, \ms, \samps);
		pitch = MGU_parameter(container, \pitch, Float, [-24, 24], 0, true, \semitones, \ratio);
		playstop.parentAccess = this; // allows access to this for parameter call back;

		env = MGU_kEnvADSR();
		env.includeIn(this);
		env.connectToParameter(level);

	}

	paramCallBack { |param, value|
		switch(param,
			\playStop, { switch(value[0],
				\play, { this.sendSynth() },
				\stop, { this.killAllSynths() })};
		);
	}

	bufferLoaded {

		startPos.range[1] = (num_frames / samplerate) * 1000;
		startPos.sr = samplerate; //
		length.default = num_frames;
		length.val = length.default;

		def = SynthDef(name, {
			var phasor, bufrd, clock;
			clock = FaustRfshClock.ar((length.kr/SampleRate.ir).reciprocal);
			phasor = Phasor.ar(clock, BufRateScale.kr(buffer.bufnum) * pitch.kr,
				startPos.kr, startPos.kr + length.kr, startPos.kr);
			bufrd = BufRd.ar(num_outputs, buffer.bufnum, phasor, loop.kr);
			bufrd = bufrd * In.kr(level.kbus);
			Out.ar(master_internal, bufrd);
		}).add;

	}
}


