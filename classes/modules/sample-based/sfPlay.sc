PO_sfPlayer : MGU_AbstractBufferModule { // simple soundFile player

	var <startPos;
	var <loop, <playstop;

	*new { |out = 0, server, num_inputs = 0, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\generator)
		.init.initModule.initMasterDef;
	}

	initModule {

		description = "simple soundfile player...";

		playstop = MGU_parameter(container, \playStop, Symbol, [\play, \stop], \stop);
		startPos = MGU_parameter(container, \startPos, Integer, [0, inf], 0, true, \ms, \samps);
		playstop.parentAccess = this; // allows access to this for parameter call back;
		loop = MGU_parameter(container, \loop, Integer, [0, 1], 1, true);

	}

	paramCallBack { |param, value|
		switch(param,
			\playStop, { switch(value[0],
				\play, { this.sendSynth },
				\stop, { this.killAllSynths })};
		);
	}

	bufferLoaded {
		startPos.range[1] = (num_frames / samplerate) * 1000;
		startPos.sr = samplerate; //
		def = SynthDef(name, {
			var bufrd;
			bufrd = PlayBuf.ar(num_outputs, buffer.bufnum, 1, 1, startPos.kr, loop.kr, 4);
			Out.ar(master_internal, bufrd);
		}).add;

	}
}


		