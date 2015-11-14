MGU_audioTester {

	// instance variables

	var <>server, <>firstChan, <>lastChan, <>hitLength, <>noiseAmp, <>outputArray;
	var <>offset;
	var routine, routine2, whiteNoiseSynth;
	var state;

	*new { |server, firstChan = 1, lastChan = 2, hitLength = 0.5, noiseAmp = 0.2, outputArray|
		^this.newCopyArgs(server, firstChan, lastChan, hitLength, noiseAmp, outputArray).init
	}

	init {

		offset = 0;

		server ?? { server = Server.default };

		SynthDef(\audioTester, { |out, amp|
			Out.ar(out, PinkNoise.ar(amp))}).add;

		routine = Routine({
			while ( {state == 1 }, {
				(lastChan - firstChan).do({|i|
					whiteNoiseSynth = Synth(\audioTester, [\out, i - 1, \amp, noiseAmp]);
					i.postln + offset;
					hitLength.yield;
					whiteNoiseSynth.free;
					hitLength.yield;
		})})});

		routine2 = Routine({
			while ({state == 1}, {
				outputArray.size.do({|i|
					whiteNoiseSynth = Synth(\audioTester, [\out, outputArray[i] - 1, \amp, noiseAmp]);
					outputArray[i].postln + offset;
					hitLength.yield;
					whiteNoiseSynth.free;
					hitLength.yield;
		})})});

	}

	play {
		state = 1;
		routine.reset;
		routine.play;
	}

	playArray {
		state = 1;
		routine2.reset;
		routine2.play;
	}

	stopArray {
		state = 0;
		routine2.stop;
		whiteNoiseSynth.free;
	}

	stop {
		state = 0;
		routine.stop;
		whiteNoiseSynth.free
	}

}