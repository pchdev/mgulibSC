MGU_scGrainBuf : MGU_AbstractBufferModule {

	var <rfsh, <grain_size, <rate, <pos, <pan;
	var <rand_pos_freq, <rand_pos_width;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 2, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\generator)
		.init.initModule.initMasterDef
	}

	initModule {

		description = "granular synthesis module \n based on standard SC GrainBuf UGen..";

		rfsh = MGU_parameter(container, \refresh_rate, Float, [0.01, 880], 4, true);
		grain_size = MGU_parameter(container, \grain_size, Float, [5, 1000], 500, true, \ms, \s);
		rate = MGU_parameter(container, \rate, Integer, [-24, 24], 0, true, \semitones, \ratio);
		pos = MGU_parameter(container, \pos, Float, [0, 1], 0, true);
		pan = MGU_parameter(container, \pan, Float, [-1, 1], 0, true);
		rand_pos_freq = MGU_parameter(container, \rand_pos_freq, Float, [0.001, 10], 0.5, true);
		rand_pos_width = MGU_parameter(container, \rand_pos_width, Float, [0, 1000], 500,
			true, \ms, \samps, 44100);

	}

	paramCallBack { |param, value|
		switch(param,
			\playStop, { switch(value[0],
				\play, { this.sendSynth() },
				\stop, { this.killAllSynths })};
		);
	}

	readFile { |path| // re-directing method
		this.readFileSeparatedChannels(path)
	}

	bufferLoaded {

		"buffer loaded".postln;

		this.num_outputs_(2);

		def = SynthDef(name, {

			var clock = Dust.ar(rfsh.kr);
			var rand_pan = Array.fill(buffer.size, {|i|
				LFNoise1.kr(1)
			});
			var gr = Array.fill(buffer.size, {|i|
				GrainBuf.ar(1, clock, grain_size.kr, buffer[i], rate.kr,
					pos.kr + LFNoise1.kr(rand_pos_freq.kr, rand_pos_width.kr/BufFrames.kr(buffer[i])), 2, 0, -1)
			});
			var pan = Pan2.ar(gr, rand_pan).sum;

			Out.ar(master_internal, pan);
		}).add;
	}

}