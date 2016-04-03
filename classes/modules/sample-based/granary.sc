MGU_grainDelay : MGU_AbstractBufferModule {

	var <num_grains;
	var <grain_size, <randpos_width, <randpos_freq, <randpan_freq;
	var <start_pos, <grain_pitch, <grain_phase;
	var <freeze;
	var <playstop;
	var <buf_env, <env;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef;
	}

	initModule {

		var bufsignal, bufsize = 16384;
		num_grains = 16;

		buffer = Buffer.alloc(server, 88200, 1);

		playstop = MGU_parameter(container, \play_stop, Symbol, [\play, \stop], \stop);
		playstop.parentAccess = this; // allows access to this for parameter call back;

		grain_size = MGU_parameter(container, \grain_size, Integer,
			[1, 1000], 500, true, \ms, \samps);
		randpos_freq = MGU_parameter(container, \randpos_freq, Float,
			[0.001, 10], 1, true);
		randpos_width = MGU_parameter(container, \randpos_width, Integer,
			[0, 1000], 250, true, \ms, \samps);
		randpan_freq = MGU_parameter(container, \randpan_freq, Float,
			[0.001, 10], 1, true);
		grain_pitch = MGU_parameter(container, \grain_pitch, Float,
			[-72, 72], 0, true,
			\semitones, \ratio);
		start_pos = MGU_parameter(container, \start_pos, Integer,
			[0, 2000], 0, true, \ms, \samps);


		freeze = MGU_parameter(container, \freeze, Integer, [0, 1], 0, true);

		env = Env([0,1,0], [1,1], \welch).asSignal(bufsize);
		buf_env = Buffer.alloc(server, bufsize);

		def = SynthDef(name, {

			var in, phs_wr, size, freqr, ramp, clock, start, end,
			randpan, phs_rd, phs_env, bufwr, bufrd, bufrd_env, process, pan;

			in = In.ar(inbus, num_inputs);

			phs_wr = MGU_ramper.ar(1, 0, BufFrames.kr(buffer.bufnum)) * freeze.kr;
			bufwr = BufWr.ar(in, buffer.bufnum, phs_wr);

			freqr = (grain_size.kr/SampleRate.ir).reciprocal * grain_pitch.kr;

			ramp = Array.fill(num_grains, {|i|
				MGU_ramper.ar(freqr, num_grains.reciprocal * i);
			});

			clock = Array.fill(num_grains, {|i|
				MGU_clock.ar(freqr, num_grains.reciprocal * i);
			});

			start = Latch.ar(start_pos.ar + LFNoise1.ar(randpos_freq.kr, randpos_width.kr), clock);

			end = Latch.ar(start + grain_size.kr, clock);
			randpan = LFNoise1.kr(randpan_freq.kr);

			phs_rd = MulAdd(ramp, grain_size.kr, start);
			phs_env = MulAdd(ramp, BufFrames.kr(buf_env.bufnum));

			bufrd = BufRd.ar(1, buffer.bufnum, phs_rd);
			bufrd_env = BufRd.ar(1, buf_env.bufnum, phs_env);

			process = bufrd * bufrd_env;
			pan = Pan2.ar(process, randpan);

			Out.ar(master_internal, pan);

		}).add;

	}

	paramCallBack { |param, value|

		case

		{ (param == \play_stop) && (value == \play) } { this.sendSynth() }
		{ (param == \play_stop) && (value == \stop) } { this.killAllSynths() };

	}

	buildGrainEnvelope {
		buf_env.sendCollection(env);
	}


}

MGU_grainSampler : MGU_AbstractBufferModule {

	var numGrains;
	var <grainSize, <randPosFreq, <randPosWidth, <randPanFreq;
	var <startPos, <grainPitch, <grainPhase;
	var <buf_env, <env;
	var <freq;
	var playstop;
	var <grainSizeMod;
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

		playstop = MGU_parameter(container, \playStop, Symbol, [\play, \stop], \stop);
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

		this.buildGrainEnvelope();

		this.num_outputs_(2);

		def = SynthDef(name, {

			var clickenv, clickgate, dsize, size, freqr, ramp, clock, start, end,
			randpan, phs_rd, phs_env, bufrd, bufrd_env, process, pan;

			size = grainSize.kr;

			freqr = (size/SampleRate.ir).reciprocal * grainPitch.kr;
			ramp = MGU_ramper.ar(freqr, 0);
			clock = MGU_clock.ar(freqr, 0);

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