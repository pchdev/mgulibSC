MGU_vuMeter {

	var <parent, <bounds, <address;
	var num_leds, <led_array;
	var background_off_color, space_size, frame_size;
	var view;
	var <value;
	var oscfunc;
	var text_display_peak, text_display_rms;

	*new { |parent, bounds, address|
		^this.newCopyArgs(parent, bounds, address).init
	}

	init {

		num_leds = 13;
		background_off_color = Color.new(0.8, 0.8, 0.8);
		frame_size = 1;
		space_size = 1;
		address = address ++ "/reply";

		view = UserView(parent, bounds).background_(Color.black);

		led_array = Array.fill(num_leds, {|i|

			var left, top, width, height, background_on_color;

			width = view.bounds.width - (frame_size * 2);
			height = (view.bounds.height - (space_size * (num_leds - 1)) - (frame_size*2))/num_leds;
			left = frame_size;
			top = frame_size + (height*i) + (space_size*i);

			UserView(view, Rect(left, top, width, height))
			.background_(background_off_color);

		});

		text_display_peak = StaticText(parent,
			Rect(bounds.left - 1, bounds.height + 17, bounds.width, 15))
		.string_("-12").font_(Font("Arial", 8)).align_(\center);

		text_display_rms = StaticText(parent,
			Rect(bounds.left - 1, bounds.height + 32, bounds.width, 15))
		.string_("-96").font_(Font("Arial", 8)).align_(\center);

		this.initOSC();
	}

	initOSC {
		oscfunc = OSCFunc({|msg|
			this.val_(msg[0], msg[1])}, address);
	}

	val_ {|peak_value, rms_value|
		peak_value = peak_value.ampdb;
		rms_value = rms_value.ampdb;
		this.drawPeak(peak_value);
	}

	drawPeak {|val|
		var index;
		if(val <= -50) { this.lowLED() } {
			index = (val/3).round(1).neg;
			if(val.isNegative) { this.overLED() } { this.reachLED(index) };
		};
	}

	// LED lighting

	clearLED {
		num_leds.do({|i|
			led_array[i].background = background_off_color;
		});
	}

	lowLED {
		this.clearLED();
		led_array[12].background = Color.blue;
	}

	overLED {
		led_array[0].background = Color.red;
	}

	reachLED {|index|
		index = 12 - index;


	}
}

	