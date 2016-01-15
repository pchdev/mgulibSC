MGU_glitchyGran : MGU_AbstractBufferModule { // from sample

	var <numGrains, <bufpos, <grainOffset;
	var <speed, <panFreq, <grainSize;
	var <rdmPosFreq, <rdmCoeff;

	*new { |out = 0, server, numInputs = 2, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\generator)
		.init.initModule.initMasterDef;
	}

	initModule {

		numGrains = MGU_parameter(container, \num_grains, Integer, [1, 100], 32, false);
		bufpos = MGU_parameter(container, \bufpos, Float, [0, inf], 0, true, \ms, \samp);
		grainOffset = MGU_parameter(container, \grain_offset,
			Integer, [0, 5000], 100, true, \ms, \samp);
		speed = MGU_parameter(container, \speed, Float, [0, 10], 1, true);
		panFreq = MGU_parameter(container, \panFreq, Integer, [0, 250], 2, true);
		grainSize = MGU_parameter(container, \grainSize, Integer, [5, 10000], 1000, true);
		rdmPosFreq = MGU_parameter(container, \rdmPosFreq, Float, [1, 10], 1, true);
		rdmCoeff = MGU_parameter(container, \rdmCoeff, Integer, [0, 10000], 500, true, \ms, \samp);
	}

	paramCallBack {

	}

	bufferLoaded {

		bufpos.range[1] = numFrames;

		def = SynthDef(name, {
			var phasor, bufrd, localbufpos, rdm_pan, process, rfsh_clock, rdm_pos;
			rdm_pos = LFNoise1.ar(rdmPosFreq.kr, rdmCoeff.kr);
			rfsh_clock = FaustRfshClock.ar(grainSize.kr/SampleRate.ir);
			//rdm_pan = LFNoise1.ar(panFreq.kr);
			localbufpos = Array.fill(numGrains.val, {|i|
				bufpos.kr + (i*grainOffset.kr)});
			phasor =  Phasor.ar(rfsh_clock, BufRateScale.kr(buffer.bufnum) * speed.kr,
					localbufpos + rdm_pos, localbufpos + grainSize.kr, 0);
			bufrd = BufRd.ar(numOutputs, buffer.bufnum, phasor, 1);
			//process = Mix.ar(bufrd)/4;
			process = [bufrd[0], bufrd[1]];
			Out.ar(master_internal, process);

		}).add;

	}



}

