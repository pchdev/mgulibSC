MGU_push_toggle {

	var name, midi_index, parser;

	*new { |name, midi_index, parser|
		^this.newCopyArgs(name, midi_index, parser).init
	}

	init {

		MIDIFunc.cc({|value, num|
			parser.parseToggle(midi_index, value)}, midi_index);

	}
}
