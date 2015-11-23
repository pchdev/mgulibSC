MGU_pushParser {

	var reaper_responder, push_responder;
	var currentchannel_array;

	var reaper_isplaying, reaper_isrecording;
	var select_mode;

	*new { |reaper_responder, push_responder|
		^this.newCopyArgs(reaper_responder, push_responder).init
	}

	init {

		currentchannel_array = [0];
		reaper_isplaying = false;

		OSCFunc({|msg, time| // redirect reaper feedback to parser's matching method
			this.parse_reaperfeedback(msg[0], msg[1]);
		}, "/play", nil, 8889)
	}

	init_push {

		var control_array = [85, 86, 87, 88, 89, 90, 116, 117, 118, 119, 9, 3, 44, 45, 46, 47, 48, 49,
		50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 110, 111, 112,
		113, 114, 115, 36, 37, 38, 39, 40, 41, 42, 43, 28, 29];
		var note_array = Array.fill(64, {|i| i+36});
		var toggle_up_array = Array.fill(8, {|i| i+20});
		var toggle_down_array = Array.fill(8, {|i| 100 + i});

		control_array.size.do({|i|
			push_responder.setControl(control_array[i], 1)
		});

		note_array.size.do({|i|
			push_responder.setPadColor(note_array[i], 0, 0, 0)
		});

		// 1 3 2 for blue/green

		toggle_up_array.size.do({|i|
			push_responder.setControl(toggle_up_array[i], 0)
		});

		toggle_down_array.size.do({|i|
			push_responder.setPadColor(toggle_down_array[i], 0, 0, 0);
		});

		push_responder.lcd_clearAll;
		push_responder.lcd_display("PCHDEV", 2);
		push_responder.lcd_display("reaper / collider", 3);

		this.initUser

	}

	initUser {

		var choice_array = [20, 21, 22, 23];
		select_mode = \init;
		push_responder.lcd_clearAll;
		push_responder.lcd_display("PCHDEV", 1);
		push_responder.lcd_display("reaper  /  collider", 2);
		push_responder.lcd_display("1 = reaper, 2 = collider", 3);
		push_responder.lcd_display("3 = max, 4 = game", 4);

		choice_array.size.do({|i|
			push_responder.setControl(choice_array[i], 2);
		});
	}

	// NOTES

	parseNoteOn { |index, velocity|

		var note = index;

		push_responder.setPadColor(index, 2, 8, 2);
		currentchannel_array.size.do({|i|
			reaper_responder.send_noteOn(note, velocity, currentchannel_array[i])
		});
	}

	parseNoteOff { |index|

		push_responder.setPadColor(index, 1, 3, 2);
		currentchannel_array.size.do({|i|
			reaper_responder.send_noteOff(index, currentchannel_array[i])
		});
	}

	// TOGGLES

	parseUpToggle { |index, value|
		switch(select_mode,
			\init, {
				switch(index,
					0, { select_mode = \reaper; this.parseInitReaper },
					1, { select_mode = \collider; this.parseInitCollider },
					2, { select_mode = \max; this.parseInitMix },
					3, { select_mode = \game; this.parseInitGame })},
			\reaper, { },
			\collider, { },
			\mix, { },
			\game, { }
		);
	}

	parseInitReaper {
		push_responder.setControl_uptoggles(\off);
		push_responder.setControl(20, 19);
		push_responder.lcd_clearAll();
		push_responder.lcd_display(" PCHD: reaper  mode selected", 2);
	}

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

	parse_reaperfeedback { |address, value|
		address = address.asSymbol;
		["from reaper", address, value].postln;
		switch(address,
			"/play", { this.parse_reaperfbk_play(value) }
		);
	}

	parse_reaperfbk_play { |value|
		if(value == 0, {
			reaper_isplaying = false;
			push_responder.setControl(85, 1);
		}, {
			reaper_isplaying = true;
			push_responder.setControl(85, 7);
		});
	}
}