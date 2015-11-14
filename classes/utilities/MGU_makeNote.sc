MGU_makeNote { // ceci est un essai

	// instance variables
	var <>synth;
	var <activeRoutines, <activeNotes;

	// class methods
	*new {|synth|
		^this.newCopyArgs(synth).init;
	}

	init {

		activeRoutines = [];
		activeNotes = [];

	}

	// instance methods

	trig { |pitch = 69, velocity = 127, duration = 2000|

		// if note already playing, stop the previous note routine, not to send note-off
		// note is automatically killed by MGU_gen when receiving the same one
		if(activeNotes.includes(pitch), {
			activeRoutines.at(activeNotes.indexOf(pitch)).stop;
			activeRoutines.removeAt(activeNotes.indexOf(pitch));
			activeNotes.removeAt(activeNotes.indexOf(pitch));
		});

		// adding note to activeNotes array
		activeNotes = activeNotes.growClear(1);
		activeNotes.put(activeNotes.size - 1, pitch);


		// adding Routine to activeRoutines array
		activeRoutines = activeRoutines.growClear(1);
		activeRoutines.put(activeRoutines.size - 1,
			Routine.new({
				synth.midinote(pitch, velocity);
				if(duration > 0, { // if 0 = sustained note : manual note-off
					(duration/1000).yield; // auto note-off
					synth.midinote(pitch, 0);
					activeRoutines.removeAt(activeNotes.indexOf(pitch));
					activeNotes.removeAt(activeNotes.indexOf(pitch));
				})
			})
		);

		activeRoutines.at(activeRoutines.size - 1).play;
	}

}

