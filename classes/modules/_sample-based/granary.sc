MGU_grainSampler : MGU_AbstractBufferModule {

	var numGrains;
	var <grainSize, <randPosFreq, <randPosWidth, <randPanFreq;
	var <startPos, <grainPitch, <grainPhase;
	var <buf_env, <env;
	var <freq;
	var playstop;
	var <grainSizeMod;
	var def2;
	var <note_group;

	*new { |out = 0, server, num_inputs = 0, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\generator)
		.init.initModule.initMasterDef;
	}

	initModule {

		var bufsignal, bufsize;
		note_group = [];
		bufsize = 16384;

		numGrains = 8;

		grainSize = MGU_parameter(container, \grainSize, Integer, [1, 1000],
			500, true, \ms, \samps);
		randPosFreq = MGU_parameter(container, \randFreq, Float, [0.001, 10], 1, true);
		randPosWidth = MGU_parameter(container, \randWidth, Integer,
			[0, 1000], 250, true, \ms, \samps);
		randPanFreq = MGU_parameter(container, \randPanFreq, Float, [0.001, 10], 1, true);
		grainPitch = MGU_parameter(container, \grainPitch, Float, [-72, 72], 0, true,
			\semitones, \ratio);
		startPos = MGU_parameter(container, \startPos, Integer,
			[0, inf], 0, true, \ms, \samps);
		grainSizeMod = MGU_parameter(container, \grainSizeMod, Float, [0, 1], 0.1, true);

		playstop = MGU_parameter(container, \playStop, Symbol, nil, \stop, true);
		playstop.parentAccess = this; // allows access to this for parameter call back;

		env = Env([0,1,0], [1,1], \welch).asSignal(bufsize);
		buf_env = Buffer.alloc(server, bufsize);

	}

	paramCallBack { |param, value|
		switch(param,
			\playStop, { switch(value[0],
				\play, { this.sendGrains },
				\stop, { this.killAllSynths })};

		);
	}

	buildGrainEnvelope {
		buf_env.sendCollection(env);
	}

	sendSynth {

		// for multiple voices in a single note (num_grains)

		note_group = note_group.add(Group(node_group));

		// if no master, create one, otherwise only send synths
		if(node_array_master[0].isNil) { node_array_master = node_array_master.add(
			Synth(name ++ "_master", master_container.makeSynthArray.asOSCArgArray,
				node_group, 'addToTail'))};

		fork({
			numGrains.do({|i|
				node_array = node_array.add(
					Synth(name, container.makeSynthArray.asOSCArgArray,
						note_group[note_group.size - 1], 'addToHead'));
				0.125.wait();
			});
		});
	}


	bufferLoaded { // callback function after readFile completion

		startPos.range[1] = (num_frames / samplerate) * 1000;
		startPos.sr = samplerate;

		1.do({|i|
			this.buildGrainEnvelope();
		});

		this.num_outputs_(2);

		def = SynthDef(name, {

			var clickenv, clickgate, dsize, size, freqr, ramp, clock, start, end,
			randpan, phs_rd, phs_env, bufrd, bufrd_env, process, pan;

			size = grainSize.kr;

			freqr = (size/SampleRate.ir).reciprocal * grainPitch.kr;
			ramp = MGU_ramper.ar(freqr);
			clock = MGU_clock.ar(freqr);

			start = Latch.ar(startPos.ar + LFNoise1.ar(randPosFreq.kr, randPosWidth.kr)
				+ In.ar(startPos.abus), clock);
			end = Latch.ar(start + size, clock);
			randpan = LFNoise1.kr(randPanFreq.kr);

			phs_rd = MulAdd(ramp, size, start);
			phs_env = MulAdd(ramp, BufFrames.kr(buf_env.bufnum));

			bufrd = BufRd.ar(1, buffer.bufnum, phs_rd);
			bufrd_env = BufRd.ar(1, buf_env.bufnum, phs_env);

			process = bufrd * bufrd_env;
			pan = Pan2.ar(process, randpan);

			Out.ar(master_internal, pan);

		}).add;


	}




}