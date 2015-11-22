MGU_pushParser {

	var reaper_responder, push_responder;
	var currentchannel_array;

	var reaper_isplaying, reaper_isrecording;

	*new { |reaper_responder, push_responder|
		^this.newCopyArgs(reaper_responder, push_responder).init
	}

	init {
		currentchannel_array = [0];
		reaper_isplaying = false;
	}

	// NOTES

	parseNoteOn { |index, velocity|

		var note = index;

		push_responder.setPadColor(index, 1, 4, 1);
		currentchannel_array.size.do({|i|
			reaper_responder.send_noteOn(note, velocity, currentchannel_array[i])
		});
	}

	parseNoteOff { |index|

		push_responder.setPadColor(index, 0, 0, 0);
		currentchannel_array.size.do({|i|
			reaper_responder.send_noteOff(index, currentchannel_array[i])
		});
	}

	// TOGGLES

	// CC - KNOBS

	// CONTROLS

	parseControl { |name, value|
		switch(name,
			"play", { this.parsePlay(value)},
			"record", { this.parseRecord(value)},
			"new", { this.parseNew(value)}
		);
	}

	parsePlay { |value|

		if(value == 0, {
			if(reaper_isplaying, {
				reaper_isplaying = false;
				reaper_responder.stop;
				push_responder.setControl(85, 1);
			}, {
				reaper_isplaying = true;
				reaper_responder.play;
				push_responder.setControl(85, 127);
			});
		}, { // else 127 : do nothing except blink
			push_responder.setControl(85, 5)});
	}

	// FEEDBACK FROM REAPER


}