MGU_push_controlButton {

	var name, midi_index, parser;

	*new { |name, midi_index, parser|
		^this.newCopyArgs(name, midi_index, parser).init
	}

	init {
		MIDIFunc.cc({|value, num|
			parser.parseControl(name, value)}, midi_index);
	}

}