MGU_scGrainBuf : MGU_AbstractBufferModule {

	var <rfsh, <grain_size, <rate, <pos, <pan;
	var <rand_pos_freq, <rand_pos_width;

	*new { |out = 0, server, numInputs = 1, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\generator)
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
		rand_pos_width = MGU_parameter(container, \rand_pos_width, Float, [0.0001, 0.01], 0.005, true);

	}

	paramCallBack { |param, value|
		switch(param,
			\playStop, { switch(value[0],
				\play, { this.sendSynth() },
				\stop, { this.killAllSynths })};
		);
	}

	bufferLoaded {

		this.numOutputs_(2);

		def = SynthDef(name, {
			var clock = Impulse.ar(rfsh.kr);
			var gr = GrainBuf.ar(2, clock, grain_size.kr, buffer, rate.kr,
				pos.kr + LFNoise1.kr(rand_pos_freq.kr, rand_pos_width.kr * pos.kr), 2, pan.kr, -1);
			Out.ar(master_internal, gr);
		}).add;
	}

}