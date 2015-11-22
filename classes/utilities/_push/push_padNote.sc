MGU_push_padNote {

	var name, midi_index, parser;

	*new { |name, midi_index, parser|
		^this.newCopyArgs(name, midi_index, parser).init
	}

	init {

		MIDIFunc.noteOn({|vel, index|
			["push", name, "noteOn", vel].postln;
			parser.parseNoteOn(index, vel);
		}, midi_index);

		MIDIFunc.noteOff({|vel, index|
			["push", name, "noteOff"].postln;
			parser.parseNoteOff(index, 0);
		}, midi_index);

	}

}

	