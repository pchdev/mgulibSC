MGU_CCDict {

	var dict, inv_dict;

	*new {
		^this.new.init
	}

	init {
		dict =  (
			play: 85,
			record: 86,
			new: 87,
			duplicate: 88,
			automation: 89,
			fixed_length: 90,
			quantize: 116,
			double: 117,
			delete: 118,
			undo: 119,
			metronome: 9,
			tap_tempo: 3,
			left_arrow: 44,
			right_arrow: 45,
			up_arrow: 46,
			down_arrow: 47,
			select: 48,
			shift: 49,
			note: 50,
			session: 51,
			add_effect: 52,
			add_track: 53,
			octave_down: 54,
			octave_up: 55,
			repeat: 56,
			accent: 57,
			scales: 58,
			user: 59,
			mute: 60,
			solo: 61,
			next: 62,
			previous: 63,
			device: 110,
			browse: 111,
			track: 112,
			clip: 113,
			volume: 114,
			pan_send: 115,
			fourth: 36,
			fourth_t: 37,
			eighth: 38,
			eighth_t: 39,
			sixteenth: 40,
			sixteenth_t: 41,
			thirtysecond: 42,
			thirtysecond_t: 43,
			master: 28,
			stop: 29
		);

		inv_dict = dict.invert();

	}

	getCCNumFromName { |name|
		^dict.at(name)
	}

	getCCNameFromNum { |num|
		^inv_dict.at(num)
	}


}

MGU_CCModeDict {

	var dict;

	*new {
		^this.new.init
	}

	init {
		dict =  (
			off: 0,
			dim: 1,
			dim_slow: 2,
			dim_fast: 3,
			full: 4,
			full_slow: 5,
			full_fast: 6,
			red_dim: 1,
			red_dim_slow: 2,
			red_dim_fast: 3,
			red_full: 4,
			red_full_slow: 5,
			red_full_fast: 6,
			orange_dim: 7,
			orange_dim_slow: 8,
			orange_dim_fast: 9,
			orange_full: 10,
			orange_full_slow: 11,
			orange_full_fast: 12,
			yellow_dim: 13,
			yellow_dim_slow: 14,
			yellow_dim_fast: 15,
			yellow_full: 16,
			yellow_full_slow: 17,
			yellow_full_fast: 18,
			green_dim: 19,
			green_dim_slow: 20,
			green_dim_fast: 21,
			green_full: 22,
			green_full_slow: 23,
			green_full_fast: 24
		);

	}

	getCCModeNumFromName { |name|
		^dict.at(name)
	}

}





