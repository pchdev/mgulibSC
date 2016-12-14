MGU_sfPlayer : MGU_AbstractBufferModule { // simple soundFile player

	var <start_pos;
	var <loop;
	var <rate;

	*new { |out = 0, server, num_inputs = 0, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\generator)
		.init.initModule.initMasterDef;
	}

	initModule {

		description = "simple soundfile player...";

		start_pos = MGU_parameter(container, \start_pos, Integer, [0, inf], 0, true, \ms, \samps);
		loop = MGU_parameter(container, \loop, Integer, [0, 1], 1, true);
		rate = MGU_parameter(container, \rate, Float, [-16, 16], 1, true);

	}

	bufferLoaded {
		start_pos.range[1] = (num_frames / samplerate) * 1000;
		start_pos.sr = samplerate; //
		def = SynthDef(name, {
			var playbuf = PlayBuf.ar(num_outputs, buffer.bufnum, rate.kr, 1, start_pos.kr, loop.kr, 2);
			Out.ar(master_internal, playbuf);
		}).add;

	}
}

MGU_groover : MGU_AbstractBufferModule { // more complex player

	var <start_pos, <end_pos;
	var <loop, <rate, <pitch, <pause;

	*new { |out = 0, server, num_inputs = 0, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\generator)
		.init.initModule.initMasterDef;
	}

	initModule {

		description = "buffer reader";

		start_pos = MGU_parameter(container, \start_pos, Integer, [0, inf], 0, true, \ms, \samps);
		end_pos = MGU_parameter(container, \end_pos, Integer, [0, inf], 1000, true, \ms, \samps);
		loop = MGU_parameter(container, \loop, Integer, [0, 1], 1, true);
		rate = MGU_parameter(container, \rate, Float, [-16, 16], 1, true);
		rate.parentAccess_(this);
		pitch = MGU_parameter(container, \pitch, Float, [-24, 24], 0, true, \semitones, \ratio);
		pitch.parentAccess_(this);
		pause = MGU_parameter(container, \pause, Integer, [0, 1], 0, true);

	}

	parameterCallBack { |parameter_name, value|
		switch(parameter_name)
		{ \rate } { pitch.val_(MGU_conversionLib.ratio_st(value), callback: false) } // to avoid overflow
		{ \pitch } { rate.val_(MGU_conversionLib.st_ratio(value), callback: false) };
	}

	bufferLoaded { // add a crossfade for loops + make it in c++

		start_pos.range[1] = num_frames/samplerate * 1000;
		end_pos.range[1] = start_pos.range[1];
		start_pos.sr = samplerate;
		end_pos.val = num_frames;

		def = SynthDef(name, {
			var freq, free_imp, imp, phs, bufrd, first_sample;
			first_sample = Line.kr(0, 1, 0.05).floor(); // <- better solution to be found
			freq = (end_pos.kr - start_pos.kr / SampleRate.ir).reciprocal;
			imp = Impulse.ar(freq);
			free_imp = Impulse.kr(freq);
			phs = Phasor.ar(imp, rate.kr * (1 - pause.kr), start_pos.kr, end_pos.kr, start_pos.kr);
			bufrd = BufRd.ar(num_outputs, buffer.bufnum, phs, 0, 2);
			FreeSelf.kr(free_imp * (1-loop.kr) * first_sample);
			Out.ar(master_internal, bufrd);

		}).add;
	}
}


		