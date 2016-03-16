PO_granaryMTS : MGU_AbstractModule { // granular delay, mono to stereo -> ParOral

	var numGrains, bufsize;
	var grainSize, randPosFreq, randPosWidth, randPanFreq;
	var startPos, grainPitch, freeze, grainModAmount;
	var playStop;
	var <buffer, <buf_env;


	*new { |out = 0, server, numInputs = 1, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		var env_bufsignal, env_bufsize;
		numGrains = 8;

		grainSize = MGU_parameter(container, \grainSize, Integer, [1, 1000], 500, true, \ms, \samps);
		randPosFreq = MGU_parameter(container, \randFreq, Float, [0.001, 10], 1, true);
		randPosWidth = MGU_parameter(container, \randWidth, Integer, [0, 2000], 500, true, \ms, \samps);
		randPanFreq = MGU_parameter(container, \randPanFreq, Float, [0.001, 10], 1, true);
		grainPitch = MGU_parameter(container, \grainPitch, Float, [-24, 24], 0, true, \semitones, \ratio);
		startPos = MGU_parameter(container, \startPos, Integer, [0, inf], 0, true, \ms, \samps);
		grainModAmount = MGU_parameter(container, \grainModAmount, Float, [0, 1], 0, true);
		freeze = MGU_parameter(container, \freeze, Integer, [0, 1], 0, true);

		playStop =  MGU_parameter(container, \playStop, Symbol, nil, nil, true);
		playStop.parentAccess = this;

		env_bufsize = 16384;
		env_bufsignal = Env([1,0,1], [1,1]).asSignal(env_bufsize);
		buf_env = Buffer.alloc(server, env_bufsize);
		buf_env.sendCollection(env_bufsignal);

		bufsize = server.sampleRate * 2;
		buffer = Buffer.alloc(server, bufsize);


		def = SynthDef(name, {

			// writing
			var in = In.ar(inbus, 1);
			var clock_wr = Impulse.ar(BufFrames.kr(buffer.bufnum).reciprocal);
			var phs_wr = Phasor.ar(clock_wr, 1, 0, BufFrames.kr(buffer.bufnum), 0);
			var bufwr = BufWr.ar(in, buffer.bufnum, phs_wr);

			// reading
			var rdm_grain = grainSize.kr + LFNoise1.ar(1, grainSize.kr * grainModAmount.kr);
			var clock = FaustRfshClock.ar((rdm_grain/SampleRate.ir).reciprocal);
			var rdm_grain2 = Latch.ar(rdm_grain, clock);
			var randpos = Latch.ar(LFNoise1.ar(randPosFreq.kr, randPosWidth.kr), clock);
			var randpan = LFNoise1.ar(randPanFreq.kr);

			var start = startPos.kr + randpos;
			var end = start + rdm_grain2;

			var phs_rd = Phasor.ar(clock, BufRateScale.kr(buffer.bufnum) * grainPitch.kr, start, end,
					start);

			var phs_env = Phasor.ar(clock, BufRateScale.kr(buf_env.bufnum) *
				(BufFrames.kr(buf_env.bufnum)/rdm_grain2) * grainPitch.kr,
				0, BufFrames.kr(buf_env.bufnum), 0);

			var bufrd_env = BufRd.ar(1, buf_env.bufnum, phs_env);
			var bufrd = BufRd.ar(1, buffer.bufnum, phs_rd);

			var process = bufrd * bufrd_env;
			var pan = Pan2.ar(process, randpan);

			Out.ar(master_internal, pan);

		}).add;
	}

	paramCallBack { |param, value|
				switch(param,
			\playStop, { switch(value[0],
				\play, { this.sendGrains },
				\stop, { this.killAllSynths })};
		);
	}

	sendSynth {

		nodeArray_master = nodeArray_master.add(
			Synth(name ++ "_master", [name ++ "_level", level.val],
				nodeGroup, 'addToTail'));
		fork({
			numGrains.do({|i|
				nodeArray = nodeArray.add(
					Synth(name, container.makeSynthArray.asOSCArgArray, nodeGroup, 'addToHead'));
				0.125.wait();

			});
		});
	}


}

MGU_grainSampler : MGU_AbstractBufferModule {

	var numGrains;
	var <grainSize, <randPosFreq, <randPosWidth, <randPanFreq;
	var <startPos, <grainPitch, <grainPhase;
	var <buf_env;
	var <freq;
	var playstop;
	var <grainSizeMod;
	var def2;

	*new { |out = 0, server, numInputs = 0, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\generator)
		.init.initModule.initMasterDef;
	}

	initModule {

		var bufsignal, bufsize;

		numGrains = 16;

		grainSize = MGU_parameter(container, \grainSize, Integer, [1, 1000],
			500, true, \ms, \samps);
		randPosFreq = MGU_parameter(container, \randFreq, Float, [0.001, 10], 1, true);
		randPosWidth = MGU_parameter(container, \randWidth, Integer,
			[0, 1000], 250, true, \ms, \samps);
		randPanFreq = MGU_parameter(container, \randPanFreq, Float, [0.001, 10], 1, true);
		grainPitch = MGU_parameter(container, \grainPitch, Float, [-24, 24], 0, true, \semitones, \ratio);
		startPos = MGU_parameter(container, \startPos, Integer,
			[0, inf], 0, true, \ms, \samps);
		grainSizeMod = MGU_parameter(container, \grainSizeMod, Float, [0, 1], 0.1, true);
		//randSizeFreq = MGU_parameter(container, \grainModAmount, Float, [0, 1], 0, true);


		playstop = MGU_parameter(container, \playStop, Symbol, nil, \stop, true);
		playstop.parentAccess = this; // allows access to this for parameter call back;

		bufsize = 16384;
		bufsignal = Env([0,1,0], [1,1]).asSignal(bufsize);
		//bufsignal = Env.asr.asSignal(bufsize);

		buf_env = Buffer.alloc(server, bufsize);
		buf_env.sendCollection(bufsignal);

	}

	paramCallBack { |param, value|
		switch(param,
			\playStop, { switch(value[0],
				\play, { this.sendGrains },
				\stop, { this.killAllSynths })};

		);
	}

	sendSynth {

		nodeArray_master = nodeArray_master.add(
			Synth(name ++ "_master", [name ++ "_level", level.val],
				nodeGroup, 'addToTail'));
		fork({
			numGrains.do({|i|
				nodeArray = nodeArray.add(
					Synth(name, container.makeSynthArray.asOSCArgArray, nodeGroup, 'addToHead'));
				0.125.wait();

			});
		});
	}


	bufferLoaded {

		startPos.range[1] = (numFrames / sampleRate) * 1000;
		startPos.sr = sampleRate;

		this.numOutputs_(2);

		def = SynthDef(name, {

			var clickenv, clickgate, dsize, size, freqr, ramp, clock, start, end,
			randpan, phs_rd, phs_env, bufrd, bufrd_env, process, pan;

			size = MGU_smoother.ar(grainSize.ar, 100);

			clickgate = BinaryOpUGen('!=', grainSize.ar, Delay1.ar(grainSize.ar));
			clickenv = EnvGen.ar(Env([1,0,1], [0.1, 0.1]), clickgate) * 2 - 1;

			freqr = (size/SampleRate.ir).reciprocal * grainPitch.kr;
			ramp = MGU_ramper.ar(freqr);
			clock = MGU_clock.ar(freqr);

			start = Latch.ar(startPos.ar + LFNoise1.ar(randPosFreq.kr, randPosWidth.kr), clock);
			//var start = startPos.kr;
			end = Latch.ar(start + size, clock);
			randpan = LFNoise1.kr(randPanFreq.kr);

			phs_rd = MulAdd(ramp, size, start);
			phs_env = MulAdd(ramp, BufFrames.kr(buf_env.bufnum));

			bufrd = BufRd.ar(1, buffer.bufnum, phs_rd);
			bufrd_env = BufRd.ar(1, buf_env.bufnum, phs_env);

			process = DelayN.ar(bufrd * bufrd_env, 0.005, 0.005) * clickenv;
			pan = Pan2.ar(process, randpan);

			Out.ar(master_internal, pan);

		}).add;


	}




}

	