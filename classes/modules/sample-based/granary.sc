MGU_grainSampler : MGU_AbstractBufferModule {

	var num_grains;
	var <grain_size, <randpos_freq, <randpos_width, <randpan_freq;
	var <start_pos, <grain_pitch, <grainPhase;
	var <buf_env, <env;
	var <freq;
	var playstop;
	var <note_group;

	*new { |out = 0, server, num_inputs = 0, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\generator)
		.init.initModule.initMasterDef;
	}

	initModule {

		var bufsignal, bufsize;
		note_group = [];
		bufsize = 16384;

		num_grains = 8;

		grain_size = MGU_parameter(container, \grain_size, Integer, [1, 1000],
			500, true, \ms, \samps);
		randpos_freq = MGU_parameter(container, \randpos_freq, Float, [0.001, 10], 1, true);
		randpos_width = MGU_parameter(container, \randpos_width, Integer,
			[0, 1000], 250, true, \ms, \samps);
		randpan_freq = MGU_parameter(container, \randpan_freq, Float, [0.001, 10], 1, true);
		grain_pitch = MGU_parameter(container, \grain_pitch, Float, [-72, 72], 0, true,
			\semitones, \ratio);
		start_pos = MGU_parameter(container, \start_pos, Integer,
			[0, inf], 0, true, \ms, \samps);

		playstop = MGU_parameter(container, \playstop, Symbol, [\play, \stop], \stop);
		playstop.parentAccess = this; // allows access to this for parameter call back;

		env = Env([0,1,0], [1,1], \welch).asSignal(bufsize);
		buf_env = Buffer.alloc(server, bufsize);

	}

	paramCallBack { |param, value|
		switch(param,
			\play_stop, { switch(value[0],
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
			num_grains.do({|i|
				node_array = node_array.add(
					Synth(name, container.makeSynthArray.asOSCArgArray,
						note_group[note_group.size - 1], 'addToHead'));
				0.125.wait();
			});
		});
	}


	bufferLoaded { // callback function after readFile completion

		start_pos.range[1] = (num_frames / samplerate) * 1000;
		start_pos.sr = samplerate;

		this.buildGrainEnvelope();

		this.num_outputs_(2);

		def = SynthDef(name, {

			var clickenv, size, freqr, ramp, clock, start, end,
			randpan, phs_rd, phs_env, bufrd, bufrd_env, process, pan;

			size = grain_size.kr;

			freqr = (size/SampleRate.ir).reciprocal * grain_pitch.kr;
			ramp = MGU_ramper.ar(freqr, 0);
			clock = MGU_clock.ar(freqr, 0);

			start = Latch.ar(start_pos.ar + LFNoise1.ar(randpos_freq.kr, randpos_width.kr)
				+ In.ar(start_pos.abus), clock);
			end = Latch.ar(start + size, clock);
			randpan = LFNoise1.kr(randpan_freq.kr);

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

MGU_grainDelay2 : MGU_AbstractBufferModule {

	var <grain_size, <randpos_freq, <randpos_width, <randpan_freq;
	var <start_pos, <grain_pitch, <freeze;
	var <freq, <playstop, <period;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef;
	}

	initModule {

		buffer = Buffer.alloc(server, 44100, 1);
		grain_size = MGU_parameter(container, \grain_size, Integer,
			[1, 1000], 500, true, \ms, \s);
		randpos_freq = MGU_parameter(container, \randpos_freq, Float,
			[0.001, 10], 1, true);
		randpos_width = MGU_parameter(container, \randpos_width, Integer,
			[0, 1000], 250, true, \ms, \s);
		randpan_freq = MGU_parameter(container, \randpan_freq, Float,
			[0.001, 10], 1, true);
		grain_pitch = MGU_parameter(container, \grain_pitch, Float,
			[-72, 72], 0, true,
			\semitones, \ratio);
		start_pos = MGU_parameter(container, \start_pos, Float,
			[0, 1], 0, true, \ms, \samps, 44100);
		freeze = MGU_parameter(container, \freeze, Integer,
			[0, 1], 0, true);
		freq = MGU_parameter(container, \freq, Integer,
			[0.25, 500], 20, true);
		period = MGU_parameter(container, \period, Integer, [5, 1000], 50, true, \period_ms, \freq);

		playstop = MGU_parameter(container, \playstop, Symbol, [\play, \stop], \stop);
		playstop.parentAccess = this; // allows access to this for parameter call back;

		def = SynthDef(name,  {
			var in, phs_wr, trig, bufwr, start, grainbuf;
			in = In.ar(inbus, 1);
			phs_wr = Phasor.ar(Impulse.ar(1), BufRateScale.kr(buffer.bufnum), 0, 44100, 0)
			* (1 - freeze.kr);
			bufwr = BufWr.ar(in, buffer.bufnum, phs_wr, 0);
			trig = Impulse.ar(freq.kr);
			start = start_pos.kr + LFNoise1.kr(randpos_freq.kr, randpos_width.kr);
			grainbuf = GrainBuf.ar(1, trig, grain_size.kr, buffer, grain_pitch.kr, start,
				2, 0, -1, 512);
			pan = Pan2.ar(grainbuf, 0);
			Out.ar(master_internal, pan);
		}).add;
	}

}


MGU_grainDelay : MGU_AbstractBufferModule {

	var num_grains;
	var <grain_size, <randpos_freq, <randpos_width, <randpan_freq;
	var <start_pos, <grain_pitch, <freeze;
	var <buf_env, <env;
	var <freq;
	var playstop;
	var <note_group;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef;
	}

	initModule {

		var bufsignal, bufsize;
		note_group = [];
		bufsize = 16384;

		num_grains = 8;

		buffer = Buffer.alloc(server, 132300, 1);

		grain_size = MGU_parameter(container, \grain_size, Integer, [1, 1000],
			500, true, \ms, \samps);
		randpos_freq = MGU_parameter(container, \randpos_freq, Float, [0.001, 10], 1, true);
		randpos_width = MGU_parameter(container, \randpos_width, Integer,
			[0, 1000], 250, true, \ms, \samps);
		randpan_freq = MGU_parameter(container, \randpan_freq, Float, [0.001, 10], 1, true);
		grain_pitch = MGU_parameter(container, \grain_pitch, Float, [-72, 72], 0, true,
			\semitones, \ratio);
		start_pos = MGU_parameter(container, \start_pos, Integer,
			[0, 1000], 0, true, \ms, \samps, 44100);
		freeze = MGU_parameter(container, \freeze, Integer, [0, 1], 0, true);


		playstop = MGU_parameter(container, \playstop, Symbol, [\play, \stop], \stop);
		playstop.parentAccess = this; // allows access to this for parameter call back;

		env = Env([0,1,0], [1,1], \welch).asSignal(bufsize);
		buf_env = Buffer.alloc(server, bufsize);

		this.buildGrainEnvelope();

		def = SynthDef(name, {

			var in, size, freqr, ramp, clock, start, end,
			randpan, phs_wr, phs_rd, phs_env, bufrd, bufwr, bufrd_env, process, pan;

			size = grain_size.kr;

			in = In.ar(inbus, 1);
			phs_wr = Phasor.ar(Impulse.ar(1), BufRateScale.kr(buffer.bufnum), 0, 132300, 0)
			* (1 - freeze.kr);
			bufwr = BufWr.ar(in, buffer.bufnum, phs_wr, 0);

			freqr = (size/SampleRate.ir).reciprocal * grain_pitch.kr;
			ramp = MGU_ramper.ar(freqr, 0);
			clock = MGU_clock.ar(freqr, 0);

			start = Latch.ar(start_pos.ar + LFNoise1.ar(randpos_freq.kr, randpos_width.kr)
				+ In.ar(start_pos.abus), clock);
			end = Latch.ar(start + size, clock);
			randpan = LFNoise1.kr(randpan_freq.kr);

			phs_rd = MulAdd(ramp, size, start);
			phs_env = MulAdd(ramp, BufFrames.kr(buf_env.bufnum));

			bufrd = BufRd.ar(1, buffer.bufnum, phs_rd);
			bufrd_env = BufRd.ar(1, buf_env.bufnum, phs_env);

			process = bufrd * bufrd_env;
			pan = Pan2.ar(process, randpan);

			Out.ar(master_internal, pan);

		}).add;
	}

	paramCallBack { |param, value|
		switch(param,
			\play_stop, { switch(value[0],
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
			num_grains.do({|i|
				node_array = node_array.add(
					Synth(name, container.makeSynthArray.asOSCArgArray,
						note_group[note_group.size - 1], 'addToHead'));
				0.125.wait();
			});
		});
	}

	readFile{}
}

