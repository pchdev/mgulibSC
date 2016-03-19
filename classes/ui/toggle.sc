MGU_toggle {

	var parent, bounds, bound_parameter;
	var frame, view;
	var on_color;
	var value;
	var parameter_address_display;

	*new { |parent, bounds, bound_parameter|
		^this.newCopyArgs(parent, bounds, bound_parameter).init
	}

	init {

		bounds ?? { bounds = Rect(20, 20, 17, 17) };
		on_color = Color.new255(55,90,101);

		frame = UserView(parent, bounds);
		view = UserView(frame, Rect(1, 1, bounds.width - 1, bounds.height - 1));

		frame.drawFunc = {
			Pen.width = 0.5;
			Pen.color = Color.black;
			Pen.strokeRect(Rect(0, 0, bounds.width, bounds.height))
		};

		bound_parameter ?? { Error("toggle is not linked to any parameter").throw; };
		this.bind_to_parameter(bound_parameter);

		view.mouseDownAction = { |me, x, y, mod, bn, cc|
			if(value == 0) { value = 1 } { value = 0 };
			bound_parameter.val_(value, report_to_ui: false);
			this.refresh_view();
		};

		parameter_address_display = StaticText(parent, Rect(bounds.left + bounds.width + 15,
			bounds.top + (frame.bounds.height/4) - 1, bound_parameter.address.size * 11, 12));
		parameter_address_display.font = Font("Arial", 11);
		parameter_address_display.align = \topLeft;
		parameter_address_display.string = bound_parameter.address;
		parameter_address_display.canFocus = true;

	}

	bind_to_parameter { |parameter|
		parameter.bound_to_ui = true;
		parameter.ui = this;
		value = parameter.val;
		this.refresh_view();
	}

	refresh_view {
		if(value == 0) { this.toggle_display_off() } { this.toggle_display_on() }
	}

	toggle_display_on {

		view.drawFunc = {
			Pen.width = 0.5;
			Pen.fillColor = on_color;
			Pen.addRect(Rect(bounds.width/5 - 1, bounds.width/5 - 1,
			bounds.width - ((bounds.width/5) * 2), bounds.height - ((bounds.height/5) * 2)));
			Pen.fillStroke;
		};
		view.refresh;
	}

	toggle_display_off {

		view.drawFunc = {
			Pen.width = 0.5;
			Pen.fillColor = Color.white;
			Pen.addRect(Rect(bounds.width/5 - 1, bounds.width/5 - 1,
			bounds.width - ((bounds.width/5) * 2), bounds.height - ((bounds.height/5) * 2)));
			Pen.fillStroke;
		};
		view.refresh;
	}

	value_from_parameter { |v|
		value = v;
		AppClock.sched(0, {
			this.refresh_view();
		});
	}



}