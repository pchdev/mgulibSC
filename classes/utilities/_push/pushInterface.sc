MGU_pushInterface {

	var <oscip, <oscport;
	var <topCCArray, <topSwitchesArray_up, <topSwitchesArray_down;

	var tempoKnob, swingKnob;

	var playButton, recordButton, newButton;
	var duplicateButton, automationButton, fixedLengthButton, quantizeButton;
	var doubleButton, deleteButton, undoButton;
	var metronomeButton, tapTempoButton;
	var leftArrowButton, rightArrowButton, upArrowButton, downArrowButton;
	var selectButton, shiftButton;
	var noteButton, sessionButton, addEffectButton, addTrackButton;
	var octaveDownButton, octaveUpButton, repeatButton, accentButton;
	var scalesButton, userButton;
	var muteButton, soloButton;
	var nextButton, previousButton;
	var deviceButton, browseButton;
	var trackButton, clipButton;
	var volumeButton, panSendButton;
	var masterButton, stopButton;
	var oneFourthButton, oneFourthTButton;
	var oneEighthButton, oneEighthTButton;
	var oneSixteenthButton, oneSixteenthTButton;
	var oneThirtySecondButton, oneThirtySecondTButton;

	var <noteMatrix;
	var netaddr, midiout;
	var <pushLCD;

	*new { |oscip, oscport|
		^this.newCopyArgs(oscip, oscport).init;
	}

	init {

		"[push] : initiating interface".postln;
		MIDIIn.connectAll;
		oscip ?? { oscip = "127.0.0.1" };
		oscport ?? { oscport = 8888 };

		netaddr = NetAddr(oscip, oscport);
		midiout = MIDIOut(5);

		pushLCD = MGU_pushLCD(midiout);
		pushLCD.display("PCHDEV Push", 2);
		pushLCD.display("collider / Reaper", 3);

		noteMatrix = Array.fill(64, {|i|
			MGU_pushNote("/vkb_midi/0/note/" ++ (36+i), 36+i, netaddr, midiout, 0);
		});

		topCCArray = Array.fill(9, {|i|
			MGU_topCC(nil, 71 + i, 0.01, 0.0, 1.0, 0, netaddr)
		});

		pushLCD.display("top CC initiated", 4);

		topSwitchesArray_up = Array.fill(8, {|i|
			MGU_pushControlButton("/track/" ++ (i+1) ++ "/select", 20 + i,
				netaddr, midiout, 1)
		});

		topSwitchesArray_down = Array.fill(8, {|i|
			MGU_pushControlButton("/topswitch_down" ++ (i+1), 102 + i, netaddr, midiout, 1)
		});

		pushLCD.display("top switched initiated", 4);

		// left-side toggles & buttons
		playButton = MGU_pushControlButton("/play", 85, netaddr, midiout);
		recordButton = MGU_pushControlButton("/record", 86, netaddr, midiout);
		newButton = MGU_pushControlButton("/new", 87, netaddr, midiout, 1, \button);
		duplicateButton = MGU_pushControlButton("/duplicate", 88,
			netaddr, midiout, 1, \button);
		automationButton = MGU_pushControlButton("/automation", 89, netaddr, midiout);
		fixedLengthButton = MGU_pushControlButton("/fixedlength", 90, netaddr, midiout);
		quantizeButton = MGU_pushControlButton("/quantize", 116,
			netaddr, midiout, 1, \button);
		doubleButton = MGU_pushControlButton("/double", 117, netaddr, midiout, 1, \button);
		deleteButton = MGU_pushControlButton("/delete", 118, netaddr, midiout, 1, \button);
		undoButton = MGU_pushControlButton("/undo", 119, netaddr, midiout, 1, \button);

		// metronomes
		metronomeButton = MGU_pushControlButton("/metronome", 9, netaddr, midiout);
		tapTempoButton = MGU_pushControlButton("/taptempo", 3, netaddr, midiout, 1, \button);

		pushLCD.display("left-side buttons initiated", 4);

		// right-side toggles & buttons
		leftArrowButton = MGU_pushControlButton("/left", 44, netaddr, midiout, 1, \button);
		rightArrowButton = MGU_pushControlButton("/right", 45, netaddr, midiout, 1, \button);
		upArrowButton = MGU_pushControlButton("/up", 46, netaddr, midiout, 1, \button);
		downArrowButton = MGU_pushControlButton("/down", 47, netaddr, midiout, 1, \button);

		selectButton = MGU_pushControlButton("/select", 48, netaddr, midiout, 1, \button);
		shiftButton = MGU_pushControlButton("/shift", 49, netaddr, midiout, 1, \button);
		noteButton = MGU_pushControlButton("/note", 50, netaddr, midiout, 1);
		sessionButton = MGU_pushControlButton("/session", 51, netaddr, midiout, 1);
		addEffectButton = MGU_pushControlButton("/addeffect", 52, netaddr, midiout, 1, \button);
		addTrackButton = MGU_pushControlButton("/addtrack", 53, netaddr, midiout, 1, \button);

		octaveDownButton = MGU_pushControlButton("/octavedown", 54, netaddr, midiout, 1, \button);
		octaveUpButton = MGU_pushControlButton("/octaveup", 55, netaddr, midiout, 1, \button);
		repeatButton = MGU_pushControlButton("/repeat", 56, netaddr, midiout, 1);
		accentButton = MGU_pushControlButton("/accent", 57, netaddr, midiout, 1);
		scalesButton = MGU_pushControlButton("/scales", 58, netaddr, midiout, 1);
		userButton = MGU_pushControlButton("/user", 59, netaddr, midiout, 1);
		muteButton = MGU_pushControlButton("/mute", 60, netaddr, midiout, 1);
		soloButton = MGU_pushControlButton("/solo", 61, netaddr, midiout, 1);
		nextButton = MGU_pushControlButton("/next", 62, netaddr, midiout, 1, \button);
		previousButton = MGU_pushControlButton("/previous", 63, netaddr, midiout, 1, \button);

		deviceButton = MGU_pushControlButton("/device", 110, netaddr, midiout, 1);
		browseButton = MGU_pushControlButton("/browse", 111, netaddr, midiout, 1);
		trackButton = MGU_pushControlButton("/track", 112, netaddr, midiout, 1);
		clipButton = MGU_pushControlButton("/clip", 113, netaddr, midiout, 1);
		volumeButton = MGU_pushControlButton("/volume", 114, netaddr, midiout, 1);
		panSendButton = MGU_pushControlButton("/pansend", 115, netaddr, midiout, 1);

		pushLCD.display("right-side buttons initiated", 4);

		// beatvalues

		oneFourthButton = MGU_pushControlButton("/1-4", 36, netaddr, midiout, 1);
		oneFourthTButton = MGU_pushControlButton("/1-4t", 37, netaddr, midiout, 1);
		oneEighthButton = MGU_pushControlButton("/1-8", 38, netaddr, midiout, 1);
		oneEighthTButton = MGU_pushControlButton("/1-8t", 39, netaddr, midiout, 1);
		oneSixteenthButton = MGU_pushControlButton("/1-16", 40, netaddr, midiout, 1);
		oneSixteenthTButton = MGU_pushControlButton("/1-16t", 41, netaddr, midiout, 1);
		oneThirtySecondButton = MGU_pushControlButton("/1-32", 42, netaddr, midiout, 1);
		oneThirtySecondTButton = MGU_pushControlButton("/1-32t", 43, netaddr, midiout, 1);

		masterButton = MGU_pushControlButton("/masterTog", 28, netaddr, midiout, 1);
		stopButton = MGU_pushControlButton("/stop", 29, netaddr, midiout, 1);

		pushLCD.display("config ok", 4);
		pushLCD.clearLine(4);

	}

}