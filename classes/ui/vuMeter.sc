MGU_vuMeter {

	var <parent, <bounds, <address;
	var oscfunc;
	var <value, normalized_value;
	var frame_view, view;
	var view_bounds, view_current_color, view_max_height;
	var over_view, over_view_bounds, over_view_current_color;
	var text_display_peak, text_display_rms;
	var curve_factor;

	*new { |parent, bounds, address|
		^this.newCopyArgs(parent, bounds, address).init
	}

	init {

		this.initOSC();

		curve_factor = 4;
		normalized_value = [0,0];
		bounds = Rect(500, 150, 20, 160);

		frame_view = UserView(parent, bounds);

		over_view_bounds = Rect(0, 0, bounds.width, bounds.height/10);
		over_view_current_color = Color.grey;
		over_view = UserView(frame_view, over_view_bounds);

		view_current_color = MGU_colorPalette.greenVU;
		view_max_height = 143;
		view_bounds = Rect(0, over_view_bounds.height, bounds.width, view_max_height);

		view = UserView(frame_view, view_bounds);

		frame_view.drawFunc = {
			Pen.width = 1;
			Pen.color = Color.black;
			Pen.strokeRect(Rect(0, 0, bounds.width, bounds.height));
		};

		over_view.drawFunc = {
			Pen.width = 1;
			Pen.strokeColor = Color.black;
			Pen.fillColor = over_view_current_color;
			Pen.addRect(over_view_bounds);
			Pen.stroke;
			Pen.fill;
		};

		view.drawFunc = {
			var top, height, rect;
			Pen.width = 0.5;
			Pen.fillColor = view_current_color;
			top = ((1 - normalized_value[0]) * view_max_height);
			height = normalized_value[0] * view_max_height ;
			rect = Rect(0, top, bounds.width, height);
			Pen.addRect(rect);
			Pen.fillStroke;
		};

	}

	initOSC {
		oscfunc = OSCFunc({|msg|
			this.valuePeakRMS(msg[3], 0)}, address ++ "/reply");
	}

	valuePeakRMS { |peak, rms|
		var peak_bounded, rms_bounded, res;

		AppClock.sched(0, {
			peak_bounded = peak.clip(0, 1);
			rms_bounded = rms.clip(0, 1);
			if(peak < 0) {};
			if(peak > 1) {
				over_view.background = Color.red;
			};
			res = [peak_bounded, rms_bounded];
			normalized_value = res;
			view.refresh();
		});

	}

}






	