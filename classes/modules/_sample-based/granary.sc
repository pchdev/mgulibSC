PO_granaryMTS : MGU_AbstractModule { // granular delay, mono to stereo -> ParOral

	var <recLength, <grainSize, <randFreq, <randWidth, <randPanFreq;
	var <numGrains, <grainPitch, <startPos;
	var <numFrames, buffer, <loopWr;
	var <grainPhase;

	var sigRect, bufEnv;

	*new { |out = 0, server, numInputs = 1, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		recLength = MGU_parameter(container, \recLength, Float, [0, inf], 2000, true, \ms, \samps);
		grainSize = MGU_parameter(container, \grainSize, Integer, [0, inf],
			500, true, \ms, \samps);
		randFreq = MGU_parameter(container, \randFreq, Float, [0.001, 10], 1, true);
		randWidth = MGU_parameter(container, \randWidth, Integer,
			[0, 20000], 500, true, \ms, \samps);
		randPanFreq = MGU_parameter(container, \randPanFreq, Float, [0.001, 10], 1, true);
		numGrains = 16;
		grainPitch = Array.fill(numGrains, {|i|
			MGU_parameter(container, \grainPitch_ ++ (i+1), Float,
				[-24, 24], 0, true, \semitones, \ratio)});
		startPos = MGU_parameter(container, \startPos, Integer,
			[0, recLength.val], 0, true, \ms, \samps);
		loopWr = MGU_parameter(container, \loopWr, Integer, [0, 1], 1, true);
		grainPhase = Array.fill(numGrains, {|i|
			MGU_parameter(container, \grainPhase_ ++ (i+1), Float, [0, 1],
				i*numGrains.reciprocal, true)});

		buffer = Buffer.alloc(server, recLength.val, 1);

		sigRect = Signal.rectWindow(4096);
		sigRect.fade(0, 441);
		sigRect.fade(3655, 4096, 1, 0);

		bufEnv = Buffer.loadCollection(server, sigRect);


		def = SynthDef(name, {

			var in = In.ar(inbus, numInputs);

			//var in = SoundIn.ar(0);

			var randPan = Array.fill(numGrains, { LFNoise1.ar(randPanFreq.kr) });

			var clock_wr = FaustRfshClock.ar((recLength.kr/SampleRate.ir).reciprocal);
			var phs_wr = Phasor.ar(clock_wr, BufRateScale.kr(buffer.bufnum), 0, recLength.val, 0);
			var bufWr = BufWr.ar(in, buffer.bufnum, phs_wr, loopWr.kr);

			var clock_rd = FaustRfshClock.ar((grainSize.kr/SampleRate.ir).reciprocal);
			var randPos = Latch.ar(LFNoise1.ar(randFreq.kr, randWidth.kr), clock_rd);

			var phs_rd = Array.fill(numGrains, {|i|
				Phasor.ar(clock_rd, BufRateScale.kr(buffer.bufnum) * grainPitch[i].kr).poll
			});

			var bufRd = BufRd.ar(1, buffer.bufnum, MulAdd(phs_rd, grainSize.kr, startPos.kr + randPos));
			var bufRd_env = BufRd.ar(1, bufEnv.bufnum, MulAdd(phs_rd, 4096));

			var process = bufRd * bufRd_env;

			var bal = Pan2.ar(process, randPan).sum;

			Out.ar(master_internal, bal);

		}).add;



	}

}

	