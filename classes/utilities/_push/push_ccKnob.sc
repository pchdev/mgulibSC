MGU_push_ccKnob {

	var name, midi_index, parser;

	*new { |name, midi_index, parser|
		^this.newCopyArgs(name, midi_index, parser).init
	}

	init {

		// captors

		MIDIFunc.noteOn({|vel, index|
			["push", name, "captor on", vel].postln;
			parser.parseCaptor(index, true);
		}, midi_index - 71);

		MIDIFunc.noteOff({|vel, index|
			["push", name, "captor off"].postln;
			parser.parseCaptor(index, false);
		}, midi_index - 71);

		// knob

		MIDIFunc.cc({|value, num|
			parser.parseCC(midi_index - 70, value)}, midi_index);
	}

}