MGU_term_PARSER { // scanf entries parsing

	// instance variables

	var <>printf_module, <>scanf_module, <>audio_module, <>post_module;
	var <>loginState, <>passwordState, <>playState;
	var user_login, user_password;
	var prevPitch, prevVel, prevDur;
	var prevMCPitch, prevMCVel, prevMCDur;
	var prevMNPitch, prevMNVel, prevMNDur;
	var prevSentString;
	var mode;

	// class methods

	*new {
		^super.new.init
	}

	// instance methods

	init {

		prevPitch = Array.new;
		prevVel = Array.new;
		prevDur = Array.new;

		prevMCPitch = Array.new;
		prevMCVel = Array.new;
		prevMCDur = Array.new;

	}

	parse { |sentString|
		var string;

		string = sentString.drop(2); // removes the "> " at the beginning of each typed line
		if(mode.notNil, { string = mode + string });

		case

		{ string == "'" } { this.parse(prevSentString) }

		{ loginState == 1 } // login
		{ user_login = string; // record user_login
			printf_module.checkPassword; // launch password checking sequence
			scanf_module.cryptedText_(true); // enable crypted text for password
		}

		{ passwordState == 1 } // password
		{ user_password = string;
			if((user_login == "LLDRS") && (user_password == "HYSOPE5BARRE"), {
				printf_module.accessGranted(user_login);
				}, // else
				{ printf_module.authFail(user_login)});
			scanf_module.cryptedText_(false); // anyway
		}

		{ playState == 1 }

		{ playState.postln;

			case

			// verbose

			{ string.beginsWith("BUILD") } { this.buildParsing(string) }
			{ string.beginsWith("DEFINE") } { this.defParsing(string) }
			{ string.beginsWith("ADD") } { this.addParsing(string) }
			{ string.beginsWith("PLAY") } { this.playParsing(string) }
			{ string.beginsWith("SET") } { this.setParsing(string) }
			{ (string == "KILL ALL NOTES") || (string == "KAN") } { audio_module.killAllNotes }
			{ (string == "BOOT SERVER") || (string == "BS") || (string == "START SERVER") } {
				audio_module.bootServer }
			{ (string == "QUIT SERVER") || (string == "QS") || (string == "STOP SERVER") } {
				audio_module.stopServer }

			// direct action shortcuts

			{ string.beginsWith("PN") } { // PLAY STD NOTE (C, D, E, F, G, A, B)
				string = string.split($ );
				if(string.at(1) == "", { string = [] });
				this.playSTDNoteParsing(string.drop(1))}
			{ string.beginsWith("PC") } { // CHORD
				string  = string.split($ );
				if(string.at(1) == "", { string = [] });
				this.playSTDChord(string.drop(1))}
			{ string.beginsWith("PA") } { // ARP
				string = string.split($ );
				if(string.at(1) == "", { string = [] });
				this.playSTDArpeggio(string.drop(1))}

			{ string.beginsWith("PMN") } { // PLAY MIDI NOTE
				string = string.split($ );
				if(string.at(1) == "", { string = [] }); // if white spaces, empty list
				this.playMIDINoteParsing(string.drop(1))} // removing "PMN"
			{ string.beginsWith("PMC") } { // CHORD
				string = string.split($ );
				if(string.at(1) == "", { string = [] });
				this.playMIDIChordParsing(string.drop(1))} // removing "PMC"
			{ string.beginsWith("PMA") } { // ARPEGGIO
				string = string.split($ );
				if(string.at(1) == "", { string = [] });
				this.playMIDIArpeggioParsing(string.drop(1))}

			{ string.beginsWith("PF") } { // PLAY FREQ
				string = string.split($ );
				if(string.at(1) == "", { string = [] });
				this.playFreqParsing(string.drop(1))}
			{ string.beginsWith("PFC") } { // CHORD
				string = string.split($ );
				if(string.at(1) == "", { string = [] });
				this.playFreqChordParsing(string.drop(1))}
			{ string.beginsWith("PFA") } { // ARP
				string = string.split($ );
				if(string.at(1) == "", { string = [] });
				this.playFreqArpeggio(string.drop(1))};

		}; // end playState

		if(sentString != "> '", { prevSentString = sentString });

	} // end parse


	// BUILD : OSCILLATOR - SAMPLER - GRANULAR - MODULATOR - LFO - ENV
	buildParsing { |string|
		string = string.split($ );
		string = string.at(1);
		switch(string,
			("OSCILLATOR"), { audio_module.buildOscillator },
			("SAMPLER"), { audio_module.buildSampler },
			("GRANARY"), { audio_module.buildGranary },
			("MODULATOR"), { audio_module.buildModulator },
			("LFO"), { audio_module.buildLFO },
			("ENV"), { audio_module.buildENV })
	}

	// SET GLOBAL
	setParsing { |string|
		var array;
		array = string.split($ );
		array = array.drop(1); // dropping "SET"

		case

		{ array.at(1) == "FREQ" } { array.removeAt(1); this.setFreqParsing(array) }
		//{ array.at(1) == "RMFREQ" } { audio_module.setRMFreq(target, string.at(2))}
		//{ array.at(1) == "RMBAL" } { audio_module.setRMBal(target, string.at(2))}
		//{ array.at(1) == "WAVEFORM" } { audio_module.setWaveform(target, string.at(2))}

		//{ array.at(1) == "PMINDEX" }
		//{ audio_module.setPMModIndex(target, string.at(2), string.at(3))}
		//{ array.at(1) == "PMRATIO" }
		//{ audio_module.setPMHarmRatio(target, string.at(2), string.at(3))};

	}

	setFreqParsing { |array| // OSCIL1 69-81 5000
		var target, value, interpDur;
		var noteTarget, newFreq;

		target = array.at(0);
		value = array.at(1);
		interpDur = array.at(2);

		value = value.split($-);
		noteTarget = value.at(0);
		newFreq = value.at(1);

		audio_module.setFreq(target, noteTarget, newFreq, interpDur);

	}


	// DEFINE
	defParsing { |string|
		var wordArray;
		wordArray = string.split($ );
		if(wordArray.at(1) == "CONST", {
			this.constDef(wordArray.drop(2))}); // dropping DEFINE & CONST
	}

	// DEFINE CONST
	constDef { |array|
		if(array.at(0) == "MODE", {
			mode = array.at(1)});
	}

	// GLOBAL ADD
	addParsing { |string|
		var wordArray, doc, receiver;
		wordArray = string.split($ );
		wordArray = wordArray.drop(1);

		switch(wordArray.at(0),
			"RM", { this.addRMParsing(wordArray.drop(1))},
			"PM", { this.addPMParsing(wordArray.drop(1))});

	}

	// ADD RM
	addRMParsing { |array|
		var target;
		target = array.at(1);
		audio_module.addRM(target);
	}

	// ADD PM
	addPMParsing { |array|
		var target;
		target = array.at(1);
		audio_module.addPM(target);
	}

	// GENERIC PLAY
	playParsing { |string|
		var wordArray;
		wordArray = string.split($ ); // converting to array, space separator

		if(wordArray.at(1) == "MIDINOTE", {
			this.playMIDINoteParsing(wordArray.drop(2))}); // routing without "PLAY MIDINOTE"
	}

	// PLAY MIDINOTE (OR PMN)
	playMIDINoteParsing { |array|

		var pitchString, velString, durString; // string format
		var finalPitch, finalEndPitch, finalVel, finalDur; // decimal format
		var isGliss = false;

		// PARSING ARRAY OF ARGUMENTS

		switch(array.size,
			0, { // no args
				if(prevPitch.at(prevPitch.size - 1).isNil, { // if no arg & no prev. value
					pitchString = "D"}, { // else
						pitchString = prevPitch.at(prevPitch.size - 1)}); // else prev. value
				if(prevVel.at(prevVel.size - 1).isNil, {
					velString = "D"}, { // else
						velString = prevVel.at(prevVel.size - 1)});
				if(prevDur.at(prevDur.size - 1).isNil, {
					durString = "D"}, { // else
						durString = prevDur.at(prevDur.size - 1)})},
			1, { // pitch arg only
				pitchString = array.at(0);
				if(prevVel.at(prevVel.size - 1).isNil, {
					velString = "D" }, { // else
						velString = prevVel.at(prevVel.size - 1)});
				if(prevDur.at(prevDur.size - 1).isNil, {
					durString = "D"}, { // else
						durString = prevDur.at(prevDur.size - 1)})},
			2, { // pitch & vel args only
				 pitchString = array.at(0); velString = array.at(1);
				if(prevDur.at(prevDur.size - 1).isNil, {
					durString = "D"}, { // else
						durString = prevDur.at(prevDur.size - 1) })},
			3, { // all args
				 pitchString = array.at(0); velString = array.at(1); durString = array.at(2)}
		);

		// PARSING PITCH (STRING) ARGUMENT

		if(pitchString.beginsWith("'"), {
			if(prevPitch.size < pitchString.size, {
				pitchString = prevPitch.at(prevPitch.size - 1)}, { // else
			pitchString = prevPitch.at(prevPitch.size - pitchString.size) });
		});

		case

		{ pitchString == "D" } { finalPitch = 69; prevPitch = prevPitch.add(pitchString) }
		{ pitchString == "R" } { finalPitch = 127.rand; prevPitch = prevPitch.add(pitchString)}
		{ pitchString.endsWith(".R")} { // ##-##.R type format // float or int ?
			var arrayer;
			arrayer = pitchString.split($.);
			arrayer = arrayer.at(0);
			arrayer = arrayer.split($-);
			finalPitch = arrayer.at(0).asInteger.rrand(arrayer.at(1).asInteger);
			prevPitch = prevPitch.add(pitchString) }
		{ pitchString.endsWith(".G") } { // GLISSANDO
			var arrayer;
			isGliss = true;
			arrayer = pitchString.split($.);
			arrayer = arrayer.at(0); // removing ".G"
			arrayer = arrayer.split($-);
			finalPitch = arrayer.at(0).asFloat;
			finalEndPitch = arrayer.at(1).asFloat;
			prevPitch = prevPitch.add(pitchString)}

			// default
		{ finalPitch.isNil } { prevPitch = prevPitch.add(pitchString);
			finalPitch = pitchString.asFloat };

		// PARSING VELOCITY ARGUMENT

		if(velString.beginsWith("'"), {
			if(prevVel.size < velString.size, {
				velString = prevVel.at(prevVel.size - 1)}, { // else
			velString = prevVel.at(prevVel.size - velString.size) });
		});

		case

		{ velString == "R" } { finalVel = 127.rand; prevVel = prevVel.add(velString)}
		{ velString == "D" } { finalVel = 64; prevVel = prevVel.add(velString) }
		{ finalVel.isNil } { prevVel = prevVel.add(velString); finalVel = velString.asFloat;};

		// PARSING DURATION ARGUMENT

		if(durString.beginsWith("'"), {
			if(prevDur.size < durString.size, {
				durString = prevDur.at(prevDur.size - 1)}, { // else
			durString = prevDur.at(prevDur.size - durString.size) });
		});

		case

		{ durString == "CONT" } { finalDur = 0; prevDur = prevDur.add(durString) }
		{ durString == "D" } { finalDur = 3000; prevDur = prevDur.add(durString) }
		{ durString == "R" } { finalDur = 1000.rrand(10000); prevDur = prevDur.add(durString)}
		{ finalDur.isNil } { prevDur = prevDur.add(durString); finalDur = durString.asFloat };

		// SENDING TO PROPER AUDIOMODULE METHODS

		if(isGliss, {
			audio_module.playMIDIGliss(finalPitch, finalEndPitch, finalVel, finalDur);
			isGliss = false}, { // else
				audio_module.playMIDINote(finalPitch, finalVel, finalDur)});

	} // end PMN

	playMIDIChordParsing { |array|

		var pitchString, velString, durString; // string format
		var finalPitch, finalEndPitch, finalVel, finalDur; // decimal format
		var isGliss = false;

		// PARSING ARGUMENT ARRAY

		switch(array.size,
			0, { // no args
				if(prevMCPitch.at(prevMCPitch.size - 1).isNil, { // if no arg & no prev. value
					pitchString = "D"}, { // else
						pitchString = prevMCPitch.at(prevMCPitch.size - 1)}); // else prev. value
				if(prevMCVel.at(prevMCVel.size - 1).isNil, {
					velString = "D"}, { // else
						velString = prevMCVel.at(prevMCVel.size - 1)});
				if(prevMCDur.at(prevMCDur.size - 1).isNil, {
					durString = "D"}, { // else
						durString = prevMCDur.at(prevMCDur.size - 1)})},
			1, { // pitch arg only
				pitchString = array.at(0);
				if(prevMCVel.at(prevMCVel.size - 1).isNil, {
					velString = "D" }, { // else
						velString = prevMCVel.at(prevMCVel.size - 1)});
				if(prevMCDur.at(prevMCDur.size - 1).isNil, {
					durString = "D"}, { // else
						durString = prevMCDur.at(prevMCDur.size - 1)})},
			2, { // pitch & vel args only
				 pitchString = array.at(0); velString = array.at(1);
				if(prevMCDur.at(prevMCDur.size - 1).isNil, {
					durString = "D"}, { // else
						durString = prevMCDur.at(prevMCDur.size - 1) })},
			3, { // all args
				 pitchString = array.at(0); velString = array.at(1); durString = array.at(2)}
		);

		// PARSING PITCH (STRING) ARGUMENT

		if(pitchString.beginsWith("'"), {
			if(prevPitch.size < pitchString.size, { // for multiple 's
				pitchString = prevPitch.at(prevPitch.size - 1)}, { // else
			pitchString = prevPitch.at(prevPitch.size - pitchString.size) });
		});

		case

		{ pitchString == "D" } { finalPitch = [60, 64, 67]; prevPitch = prevPitch.add(pitchString) }
		{ pitchString == "R" } { "nothing".postln }
		{ pitchString.endsWith(".R")} { "nothing".postln }
		{ pitchString.endsWith(".G") } { "nothing".postln }

		// default
		{ finalPitch.isNil } { prevPitch = prevPitch.add(pitchString);
			pitchString = pitchString.split($-);
			finalPitch = pitchString };

		// PARSING VELOCITY ARGUMENT

		if(velString.beginsWith("'"), {
			if(prevVel.size < velString.size, {
				velString = prevVel.at(prevVel.size - 1)}, { // else
			velString = prevVel.at(prevVel.size - velString.size) });
		});

		case

		{ velString == "R" } { finalVel = 127.rand; prevMCVel = prevMCVel.add(velString)}
		{ velString == "D" } { finalVel = 64; prevMCVel = prevMCVel.add(velString) }
		{ finalVel.isNil } { prevMCVel = prevMCVel.add(velString); finalVel = velString.asFloat;};

		// PARSING DURATION ARGUMENT

		if(durString.beginsWith("'"), {
			if(prevDur.size < durString.size, {
				durString = prevDur.at(prevDur.size - 1)}, { // else
			durString = prevDur.at(prevDur.size - durString.size) });
		});

		case

		{ durString == "CONT" } { finalDur = 0; prevMCDur = prevMCDur.add(durString) }
		{ durString == "D" } { finalDur = 3000; prevDur = prevMCDur.add(durString) }
		{ durString == "R" } { finalDur = 1000.rrand(10000); prevMCDur = prevMCDur.add(durString)}
		{ finalDur.isNil } { prevMCDur = prevMCDur.add(durString); finalDur = durString.asFloat };

		// SENDING TO PROPER AUDIOMODULE METHODS

		if(isGliss, {
			audio_module.playMIDIGliss(finalPitch, finalEndPitch, finalVel, finalDur);
			isGliss = false}, { // else
				audio_module.playMIDIChord(finalPitch, finalVel, finalDur)});

	}

	playMIDIArpeggioParsing { |array|

		printf_module.addToCue("\n\nFUNCTION NOT IMPLEMENTED YET");

	}

	playFreqParsing { |array|

		printf_module.addToCue("\n\nFUNCTION NOT IMPLEMENTED YET");

	}

	playFreqChordParsing { |array|

		printf_module.addToCue("\n\nFUNCTION NOT IMPLEMENTED YET");

	}

	playFreqArpeggioParsing { |array|

		printf_module.addToCue("\n\nFUNCTION NOT IMPLEMENTED YET");

	}

	playSTDNoteParsing { |array|

		printf_module.addToCue("\n\nFUNCTION NOT IMPLEMENTED YET");

	}

	playSTDChordParsing { |array|

		printf_module.addToCue("\n\nFUNCTION NOT IMPLEMENTED YET");

	}

	playSTDArpeggioParsing { |array|

		printf_module.addToCue("\n\nFUNCTION NOT IMPLEMENTED YET");

	}


} // end class