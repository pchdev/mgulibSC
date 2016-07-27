MGU_PushInterface {

	var <sender, <parser;
	var port;

	*new { |sender, parser|
		^this.newCopyArgs(sender, parser).init
	}

	init {

		MIDIClient.init;

		MIDIClient.sources.postln;

		MIDIClient.sources.do({|target, i|
			if((target.device == "Ableton Push")
				&& (target.name == "User Port")) {
				"Device found!".postln;
				port = i}
		});

		MIDIIn.connectAll;

		sender.connectToDevice(port);
		parser.push = sender;

		MIDIFunc.noteOn({|vel, note|
			[note, vel].postln;
			parser.parseNoteOn(note, vel);
		});

		MIDIFunc.noteOff({|vel, note|
			parser.parseNoteOff(note)
		});

		MIDIFunc.cc({|val, num|
			parser.parseCC(num, val);
		});

		MIDIFunc.bend({|val|
			parser.parseBend(val);
		});

		MIDIFunc.touch({|val|
			parser.parseTouch(val);
		});

		MIDIFunc.polytouch({|val, num|
			parser.parsePolyTouch(num, val);
		});

	}

}

MGU_PushSender {

	var name;
	var midi_out;

	var color_dict, lighting_mode_dict, ccdict, ccmode_dict;

	*new { |name|
		^this.newCopyArgs(name).init
	}

	init {

		color_dict = (
			black: 0, lightest_white: 1, lighter_white: 2, white: 3,
			salmon: 4, red: 5, darker_red: 6, darkest_red: 7,
			beige: 8, orange: 9, darker_orange: 10, darkest_orange: 11,
			gold: 12, yellow: 13, darker_yellow: 14, darkest_yellow: 15,
			apple_green: 16, grass_green: 17, darker_grass_green: 18,
			darkest_grass_green: 19, light_blue_green: 20, green: 21,
			darker_green: 22, darkest_green: 23, sea_green: 24,
			blue_green: 25, glacier_green: 26, dark_blue_green: 27,
			blue_steel: 28, blue_green2: 29, dark_blue_green2: 30,
			darkest_blue_green: 31, blue_steel2: 32, fluo_blue: 33,
			dark_blue_green3: 34, darkest_blue_green2: 35, light_sky_blue: 36,
			sky_blue: 37, dark_sky_blue: 38, night_sky_blue: 39, sunset_blue: 40,
			sky_blue2: 41, dark_sky_blue2: 42, night_sky_blue2: 43, light_purple: 44,
			purplish_blue: 45, dark_purplish_blue: 46, darkest_purplish_blue: 47,
			light_purple_2: 48, purple: 49, dark_purple: 50, darkest_purple: 51,
			light_fuscia: 52, fuscia: 53, dark_fuscia: 54, darkest_fuscia: 55,
			lips_pink: 56, pink: 57, dark_pink: 58, darkest_pink: 59, orange_candy: 60,
			orange_mandarine: 61, orange_mango: 62, yellow_green: 63, dark_green: 64
		);

		lighting_mode_dict = (
			normal: 1, fade_in_fast: 2, fade_in_med: 3, fade_in_slow: 4,
			flash_triplet_beats: 7, cycle_colors_fast: 8,
			pulse_quarter_beats: 9, pulse_whole_beats: 10, pulse_two_beats: 11,
			flash_triplet_beats: 12, flash_eight_beats: 13, flash_quarter_beats: 14,
			flash_whole_beats: 15, flash_two_beats: 16
		);

		ccdict = (
			play: 85, record: 86, new: 87, duplicate: 88, automation: 89,
			fixed_length: 90, quantize: 116, double: 117, delete: 118, undo: 119,
			metronome: 9, tap_tempo: 3, left_arrow: 44, right_arrow: 45, up_arrow: 46,
			down_arrow: 47, select: 48, shift: 49, note: 50, session: 51,
			add_effect: 52, add_track: 53, octave_down: 54, octave_up: 55, repeat: 56,
			accent: 57, scales: 58, user: 59, mute: 60, solo: 61,
			next: 62, previous: 63, device: 110, browse: 111, track: 112,
			clip: 113, volume: 114, pan_send: 115, fourth: 36, fourth_t: 37,
			eighth: 38, eighth_t: 39, sixteenth: 40, sixteenth_t: 41, thirtysecond: 42,
			thirtysecond_t: 43, master: 28, stop: 29
		);


		ccmode_dict = (
			off: 0, dim: 1, dim_slow: 2, dim_fast: 3,
			full: 4, full_slow: 5, full_fast: 6,
			red_dim: 1, red_dim_slow: 2, red_dim_fast: 3,
			red_full: 4, red_full_slow: 5, red_full_fast: 6,
			orange_dim: 7, orange_dim_slow: 8, orange_dim_fast: 9,
			orange_full: 10, orange_full_slow: 11, orange_full_fast: 12,
			yellow_dim: 13, yellow_dim_slow: 14, yellow_dim_fast: 15,
			yellow_full: 16, yellow_full_slow: 17, yellow_full_fast: 18,
			green_dim: 19, green_dim_slow: 20, green_dim_fast: 21,
			green_full: 22, green_full_slow: 23, green_full_fast: 24
		);

	}

	connectToDevice { |port|
		midi_out = MIDIOut(port).latency_(0);
	}

	lightPad { |target, color, mode|
		var vel = color_dict.at(color);
		var chan = lighting_mode_dict.at(mode);
		midi_out.noteOn(chan, target, vel);
		[chan,target,vel].postln;
	}

	lightCCButton { |target, mode|
		var ccnum = ccdict.at(target);
		var value = ccmode_dict.at(mode);
		midi_out.control(1, ccnum, value)

	}

	lightTog1 { |target, mode|
		var value = ccmode_dict.at(mode);
		var ccnum = target + 19;
		midi_out.control(1, ccnum, value);
	}

	lightTog2 { |target, color, mode|
		var value = color_dict.at(color);
		var chan = lighting_mode_dict.at(mode);
		var ccnum = target + 101;
		midi_out.control(chan, ccnum, value)
	}

	displayLCD {

	}





}

MGU_PushParser1 {

	var <>push, <>receiver;
	var <ccdict;

	*new { |push, receiver|
		^this.newCopyArgs(push, receiver).init
	}

	init {

		ccdict = (
			play: 85, record: 86, new: 87, duplicate: 88, automation: 89,
			fixed_length: 90, quantize: 116, double: 117, delete: 118, undo: 119,
			metronome: 9, tap_tempo: 3, left_arrow: 44, right_arrow: 45, up_arrow: 46,
			down_arrow: 47, select: 48, shift: 49, note: 50, session: 51,
			add_effect: 52, add_track: 53, octave_down: 54, octave_up: 55, repeat: 56,
			accent: 57, scales: 58, user: 59, mute: 60, solo: 61,
			next: 62, previous: 63, device: 110, browse: 111, track: 112,
			clip: 113, volume: 114, pan_send: 115, fourth: 36, fourth_t: 37,
			eighth: 38, eighth_t: 39, sixteenth: 40, sixteenth_t: 41, thirtysecond: 42,
			thirtysecond_t: 43, master: 28, stop: 29
		).invert;

	}

	parseNoteOn { |note, vel|
		receiver.sendNoteOn(note, vel, 1);
		push.lightPad(note, \white, \normal);
	}

	parseNoteOff { |note|
		receiver.sendNoteOff(note, 1);
		push.lightPad(note, \black, \normal);

	}

	parseCC { |ccnum, value|
		var ccname = ccdict.at(ccnum);
	}

	parseBend { |value|

	}

	parseTouch { |value|

	}

	parsePolyTouch { |note, value|

	}

}

MGU_PushToReaper1 {

	var port, reaper;

	*new {|port = 8888|
		^this.newCopyArgs(port).init
	}

	init {
		reaper = NetAddr("127.0.0.1", port);
	}

	sendNoteOn { |note, vel, chan = 1|
		reaper.sendMsg("/vkb_midi/" ++ chan ++ "/note/" ++ note, vel);
	}

	sendNoteOff { |note, chan = 1|
		reaper.sendMsg("/vkb_midi/" ++ chan ++ "/note/" ++ note, 0);
	}

	sendCC { |ccnum, value, chan = 1|
		reaper.sendMsg("/vkb_midi/" ++ chan ++ "/cc/" ++ ccnum, value);

	}

	sendBend { |value, chan = 1|
		reaper.sendMsg("/vkb_midi/" ++ chan ++ "/pitch", value);
	}

	sendTouch { |value, chan = 1|
		reaper.sendMsg("/vkb_midi/" ++ chan ++ "/channelpressure", value);
	}

	sendPolyTouch {|note, value, chan = 1|
		reaper.sendMsg("/vkb_midi/" ++ chan ++ "/polyaftertouch/" ++ note, value);
	}

	play {
		reaper.sendMsg("/play", 1);
	}

	pause {
		reaper.sendMsg("/pause", 1);
	}

	stop {
		reaper.sendMsg("/stop", 1);
	}

	record {
		reaper.sendMsg("/record", 1);
	}

	stopRecording {
		reaper.sendMsg("/record", 0);
	}

}