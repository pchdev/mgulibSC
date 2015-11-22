MGU_padNote {

	var name, midi_index, parser;

	*new { |name, midi_index, parser|
		^this.newCopyArgs(name, midi_index, parser).init
	}

	init {

		MIDIFunc.noteOn({|vel, note|
			["push", name, "noteOn", vel].postln;
			this.valueNote(note, vel);
		}, midi_index);

		MIDIFunc.noteOff({|vel, note|
			["push", name, "noteOff"].postln;
			this.valueNote(note, 0);
		}, midi_index);

	}

}

	