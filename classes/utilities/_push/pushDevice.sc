MGU_pushDevice {

	var parser;

	var ccKnob_array, toggle_topRow_array, toggle_bottomRow_array;
	var padNote_array;

	var control_play, control_record, control_new, control_duplicate;
	var control_automation, control_fixedlength, control_quantize;
	var control_double, control_delete, control_undo;
	var control_metronome, control_taptempo;

	var control_leftarrow, control_rightarrow;
	var control_uparrow, control_downarrow;

	var control_select, control_shift, control_note;
	var control_session, control_addeffect, control_addtrack;

	var control_octavedown, control_octaveup;
	var control_repeat, control_accent;
	var control_scales, control_user;
	var control_mute, control_solo;
	var control_next, control_previous;

	var control_device, control_browse;
	var control_track, control_clip;
	var control_volume, control_pansend;

	var control_beat_fourth, control_beat_fourth_t;
	var control_beat_eighth, control_beat_eighth_t;
	var control_beat_sixteenth, control_beat_sixteenth_t;
	var control_beat_thirtysecond, control_beat_thirtysecond_t;

	var control_master, control_stop;

	*new {|parser|
		^this.newCopyArgs(parser).init
	}

	init {

		// initiating arrays

		ccKnob_array = Array.fill(9, {|i|
			MGU_push_ccKnob("ccKnob_" ++ (i+1), i+1, parser)
		});

		toggle_topRow_array = Array.fill(8, {|i|
			MGU_push_toggle("topToggle_" ++ (i+1), i+20, parser)
		});

		toggle_bottomRow_array = Array.fill(8, {|i|
			MGU_push_toggle("bottomToggle_" ++ (i+1), i+1, parser)
		});

		padNote_array = Array.fill(64, {|i|
			MGU_push_padNote("padNote_" ++ (36+i), i+36, parser)
		});

		// initiating single elements

		// left-side toggles & buttons
		control_play = MGU_push_controlButton("play", 85, parser);
		control_record = MGU_push_controlButton("record", 86, parser);
		control_new = MGU_push_controlButton("new", 87, parser);
		control_duplicate = MGU_push_controlButton("duplicate", 88, parser);
		control_automation = MGU_push_controlButton("automation", 89, parser);
		control_fixedlength = MGU_push_controlButton("fixed-length", 90, parser);
		control_quantize = MGU_push_controlButton("quantize", 116, parser);
		control_double = MGU_push_controlButton("double", 117, parser);
		control_delete = MGU_push_controlButton("delete", 118, parser);
		control_undo = MGU_push_controlButton("undo", 119, parser);

		// metronomes
		control_metronome = MGU_push_controlButton("metronome", 9, parser);
		control_taptempo = MGU_push_controlButton("tap-tempo", 3, parser);

		// right-side toggles & buttons

		control_leftarrow = MGU_push_controlButton("arrow-left", 44, parser);
		control_rightarrow = MGU_push_controlButton("arrow-right", 45, parser);
		control_uparrow = MGU_push_controlButton("arrow-up", 46, parser);
		control_downarrow = MGU_push_controlButton("arrow-down", 47, parser);

		control_select = MGU_push_controlButton("select", 48, parser);
		control_shift = MGU_push_controlButton("shift", 49, parser);
		control_note = MGU_push_controlButton("note", 50, parser);
		control_session = MGU_push_controlButton("session", 51, parser);
		control_addeffect = MGU_push_controlButton("add-effect", 52, parser);
		control_addtrack = MGU_push_controlButton("add-track", 53, parser);

		control_octavedown = MGU_push_controlButton("octave-down", 54, parser);
		control_octaveup = MGU_push_controlButton("octave-up", 55, parser);
		control_repeat = MGU_push_controlButton("repeat", 56, parser);
		control_accent = MGU_push_controlButton("accent", 57, parser);
		control_scales = MGU_push_controlButton("scales", 58, parser);
		control_user = MGU_push_controlButton("user", 59, parser);
		control_mute = MGU_push_controlButton("mute", 60, parser);
		control_solo = MGU_push_controlButton("solo", 61, parser);
		control_next = MGU_push_controlButton("next", 62, parser);
		control_previous = MGU_push_controlButton("previous", 63, parser);

		control_device = MGU_push_controlButton("device", 110, parser);
		control_browse = MGU_push_controlButton("browse", 111, parser);
		control_track = MGU_push_controlButton("track", 112, parser);
		control_clip = MGU_push_controlButton("clip", 113, parser);
		control_volume = MGU_push_controlButton("volume", 114, parser);
		control_pansend = MGU_push_controlButton("pan-send", 115, parser);

		// beatvalues

		control_beat_fourth = MGU_push_controlButton("1-4", 36, parser);
		control_beat_fourth_t = MGU_push_controlButton("1-4t", 37, parser);
		control_beat_eighth = MGU_push_controlButton("1-8", 38, parser);
		control_beat_eighth_t = MGU_push_controlButton("1-8t", 39, parser);
		control_beat_sixteenth = MGU_push_controlButton("1-16", 40, parser);
		control_beat_sixteenth_t = MGU_push_controlButton("1-16t", 41, parser);
		control_beat_thirtysecond = MGU_push_controlButton("1-32", 42, parser);
		control_beat_thirtysecond_t = MGU_push_controlButton("1-32t", 43, parser);

		control_master = MGU_push_controlButton("master", 28, parser);
		control_stop = MGU_push_controlButton("stop", 29, parser);

	}

}