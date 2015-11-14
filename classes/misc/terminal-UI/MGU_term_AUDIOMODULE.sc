MGU_term_AUDIOMODULE {

	// instance variables
	var server;
	var <>parser_module, <>printf_module, <>post_module;
	var slot1, slot2, slot3;
	var noteMaker;

	// class methods

	*new {|server|
		^this.newCopyArgs(server).init;
	}

	// instance methods

	init {
		noteMaker = MGU_makeNote(slot1);
	}

	// SERVER RELATED

	bootServer {
		if(server.serverRunning, {
			printf_module.serverActive }, {
				server.boot;
				printf_module.serverBooting})
	}

	stopServer {
		if(server.serverRunning == false, {
			printf_module.serverInactive }, {
				server.quit;
				printf_module.stopServer})
	}

	checkServer {
		if(server.serverRunning == false, {
			printf_module.serverInactive}, {
				printf_module.serverActive})
	}

	// BUILD

	buildOscillator {

		case

		{ slot1.isNil } {
			slot1 = MGU_genGroup(server, 0);
			printf_module.addToCue("\n\nOSCILLATOR BUILT & ADDED ON SLOT 1") }
		{ slot2.isNil } {
			slot2 = MGU_genGroup(server, 0);
			printf_module.addToCue("\n\nOSCILLATOR BUILT & ADDED ON SLOT 2") }
		{ slot3.isNil } {
			slot3 = MGU_genGroup(server, 0);
			printf_module.addToCue("\n\nOSCILLATOR BUILT & ADDED ON SLOT 3") }
		{ (slot1.notNil) && (slot2.notNil) && (slot3.notNil) } {
			printf_module.addToCue("\n\nCOULD NOT COMPLETE BUILDING PROCESS:", {
				printf_module.addToCue("\nNO BUILDING SLOT LEFT...")}, 0.5) };
	}

	buildSampler {
		printf_module.post("\n\nERROR! FUNCTION NOT YET IMPLEMENTED...");
	}

	buildGranary {
		printf_module.post("\n\nERROR! FUNCTION NOT YET IMPLEMENTED...");
	}

	buildLFO {
		printf_module.post("\n\nERROR! FUNCTION NOT YET IMPLEMENTED...");
	}

	buildENV {
		printf_module.post("\n\nERROR! FUNCTION NOT YET IMPLEMENTED...");
	}

	buildModulator {
		printf_module.post("\n\nERROR! FUNCTION NOT YET IMPLEMENTED...");
	}

	// ADD

	addChorus { |target|
		printf_module.post("\n\nERROR! FUNCTION NOT YET IMPLEMENTED...");

	}

	addRM { |target|
		switch(target,
			"OSCIL1", { slot1.addRM;
				printf_module.post("\n\nBUILT-IN RING MODULATION ADDED ON OSCILLATOR 1") },
			"OSCIL2", { slot2.addRM;
				printf_module.post("\n\nBUILT-IN RING MODULATION ADDED ON OSCILLATOR 2") },
			"OSCIL3", { slot3.addRM;
				printf_module.post("\n\nBUILT-IN RING MODULATION ADDED ON OSCILLATOR 3")})
	}

	addPM { |target|
		switch(target,
			"OSCIL1", { slot1.addPM;
				printf_module.post("\n\nBUILT-IN PHASE MODULATION ADDED ON OSCILLATOR 1") },
			"OSCIL2", { slot2.addPM;
				printf_module.post("\n\nBUILT-IN PHASE MODULATION ADDED ON OSCILLATOR 2") },
			"OSCIL3", { slot3.addPM;
				printf_module.post("\n\nBUILT-IN PHASE MODULATION ADDED ON OSCILLATOR 3")});
	}

	// PLAY NOTES

	playMIDINote {|pitch, velocity, duration|
		pitch.postln; velocity.postln; duration.postln;
		noteMaker.synth_(slot1);
		noteMaker.trig(pitch.asFloat, velocity.asFloat, duration.asFloat);
		post_module.post("> MIDI Note received -> pitch:" + pitch ++ ", velocity:" + velocity ++ ";\n");
	}

	playMIDIGliss {|startPitch, endPitch, velocity, duration|
		var teleLine, routine;
		teleLine = MGU_teleLine(512, startPitch.midicps, endPitch.midicps, duration, 0);
		noteMaker.synth_(slot1);
		noteMaker.trig(startPitch.asFloat, velocity.asFloat, duration.asFloat);
		post_module.post("> MIDI Gliss received -> from" + startPitch + "to" + endPitch ++
			", velocity:" + velocity ++ ";\n");
		routine = Routine.new({
			0.02.yield;
			teleLine.trig({ |value| slot1.carrier.setFreq(value)}, 20);
		}).play;
	}

	playMIDIChord {|pitchArray, velocity, duration|
		var post;
		post = ("> MIDI Chord received, notes:");
		noteMaker.synth = slot1;
		pitchArray.size.do({|i|
			noteMaker.trig(pitchArray.at(i).asFloat, velocity, duration);
			post = post + pitchArray.at(i);
		});
		post = post ++ ", velocity:" + velocity ++ ";\n";
		post_module.post(post);
	}

	// SET

	setWaveform { |target, waveform|

		switch(waveform,
			"SINE", { waveform = \sine },
			"TRIANGLE", { waveform = \triangle },
			"SQUARE", { waveform = \square },
			"SAW", { waveform = \sawtooth },
			"SAWTOOTH", { waveform = \sawtooth });

		switch(target,
			"OSCIL1", { slot1.carrier.waveform_(waveform) },
			"OSCIL2", { slot2.carrier.waveform_(waveform) },
			"OSCIL3", { slot3.carrier.waveform_(waveform) });

	}

	setFreq { |target, freqTarget, newFreq, interpDur|
		var teleLine, midiConvert;

		midiConvert = { |midiPitch|
			midiPitch = midiPitch.split($.);
			midiPitch = midiPitch.at(0);
			midiPitch = midiPitch.asFloat;
			midiPitch = midiPitch.midicps };

		// if midi argument .M -> converting to cps
		if(newFreq.endsWith(".M"), {
			newFreq = midiConvert.value(newFreq);
			freqTarget = midiConvert.value(freqTarget);
		});


		teleLine = MGU_teleLine(512, freqTarget.asFloat, newFreq.asFloat, interpDur.asFloat, 0);

		switch(target,
			"OSCIL1", { teleLine.trig({|phase| slot1.carrier.setFreq(freqTarget.asFloat, phase)}, 20)},
			"OSCIL2", { teleLine.trig({|phase| slot2.carrier.setFreq(freqTarget.asFloat, phase)}, 20)},
			"OSCIL3", { teleLine.trig({|phase| slot3.carrier.setFreq(freqTarget.asFloat, phase)}, 20)});

	}

	setRMFreq { |target, freq|
		switch(target,
			"OSCIL1", { slot1.rMod_freq_(freq.asFloat) },
			"OSCIL2", { slot2.rMod_freq_(freq.asFloat) },
			"OSCIL3", { slot3.rMod_freq_(freq.asFloat) });

	}

	setRMBal { |target, value|

	}

	setPMHarmRatio { |target, modulator, ratio|

	}

	setPMModIndex { |target, modulator, index|

	}

	// KILL

	killNote { |target, note|

	}

	killAllNotes {
		for(0, 127, {|i|
			slot1.midinote(i, 0);
			slot2.midinote(i, 0);
			slot3.midinote(i, 0);
		})
	}

}