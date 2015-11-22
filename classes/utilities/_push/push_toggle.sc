MGU_push_toggle {

	var name, midi_index, parser;

	*new { |name, midi_index, parser|
		^this.newCopyArgs(name, midi_index, parser).init
	}

	init {

		MIDIFunc.cc({|value, num|
			if(midi_index < 100, {
				parser.parseUpToggle(midi_index - 20, value)}, {
				parser.parseDownToggle(midi_index - 100, value)})}, midi_index
		);

	}
}
