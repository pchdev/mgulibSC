MGU_slider {

	var parent, bounds, bound_parameter;
	var orientation, background_color;
	var frame_view, view, value_display, parameter_address_display;
	var type, range, <>value, default_value, <graphical_value;
	var curve_factor;
	var has_focus;
	var keystring;

	*new { |parent, bounds, bound_parameter|
		^this.newCopyArgs(parent, bounds, bound_parameter).init
	}

	init {

		// default arguments

		bounds !? { if(bounds.width > bounds.height, { orientation = 0 }, { orientation = 1 }) };
		bounds ?? { if(orientation == 0, {
			bounds = Rect(0, 0, 100, 30) }, { // else vertical
				bounds = Rect(0, 0, 30, 100)}
			)};

//		background_color = Color.new255(55,90,101);
		background_color = Color.new255(115,150,171);
		curve_factor = -6;
		has_focus = false;
		keystring = "";
		bound_parameter ?? { Error("slider is not linked to any parameter").throw; };
		this.bind_to_parameter(bound_parameter);
		this.init_views;
	}

	init_views {

		var mouse_actions;

		frame_view = UserView(parent, bounds);
		view = UserView(frame_view, Rect(1, 1, bounds.width - 1, bounds.height - 1));

		frame_view.drawFunc = {
			Pen.width = 0.5;
			Pen.color = Color.black;
			Pen.strokeRect(Rect(0, 0, bounds.width, bounds.height));
		};

		if(orientation == 0, {
			view.drawFunc = { |slider|
				Pen.width = 0.5;
				Pen.fillColor = background_color;
				Pen.addRect(Rect(0, 0, graphical_value/range[1] * slider.bounds.width,
					slider.bounds.height));
				Pen.fillStroke;
		}}, { // else vertical
			view.drawFunc = { |slider|
				Pen.width = 0.5;
				Pen.fillColor = background_color;
				Pen.fillRect(Rect(0, ((range[1] - graphical_value) / range[1]) * view.bounds.height,
					view.bounds.width, (value / range[1]) * view.bounds.height));
		}});

		mouse_actions = { |me, x, y, mod, bn, cc|

			if(orientation == 0, {
				x = x.clip(0, view.bounds.width);
				if((y >= 0) && (y <= view.bounds.height)) {
					graphical_value = x/view.bounds.width * range[1];
					if(type == Integer) { graphical_value = graphical_value.round(1) };
			}}, { // else vertical
				y = y.clip(0, view.bounds.height);
				if((x >= 0) && (x <= view.bounds.width)) {
					graphical_value = range[1] - (y/view.bounds.height) * range[1];
					if(type == Integer) { graphical_value = graphical_value.round(1) };
			}});

			this.calculate_value;

			view.refresh;

			// update parameter value
			bound_parameter.val_(value, report_to_ui: false);

			// value string display
			this.refresh_displayed_value;
		};

		view.mouseDownAction = mouse_actions;
		view.mouseMoveAction = mouse_actions;

		// text display
		value_display = StaticText(frame_view, Rect(bounds.width/2 - 5, bounds.height/2 - 6,
			bounds.width/2, bounds.height/2));
		value_display.font = Font("Arial", 10, false, true);
		value_display.acceptsMouse = false;
		value_display.align = \topLeft;

		this.refresh_displayed_value();


		// parameter address display
		parameter_address_display = StaticText(parent, Rect(bounds.left + bounds.width + 15,
			bounds.top + (frame_view.bounds.height/4) - 1, bound_parameter.address.size * 11, 12));
		parameter_address_display.font = Font("Arial", 11);
		parameter_address_display.align = \topLeft;
		parameter_address_display.string = bound_parameter.address;
		parameter_address_display.canFocus = true;

		parameter_address_display.mouseDownAction = { |me, x, y, mod, bn, cc|

			if(has_focus) { this.removeFocus } { this.giveFocus };

			if(cc == 2) { value = default_value;
				this.calculate_graphical_value;
				bound_parameter.val = value;
				view.refresh;
				this.refresh_displayed_value();
			};
		};

		parameter_address_display.keyDownAction = { |me, char, mod, uni, keyc, key|
			if(has_focus) {
				if((char.isDecDigit) || (keyc == 43)) {
					keystring = keystring ++ char.asString;
					value_display.string = keystring;
				};

				if(keyc == 36) { this.parse_entered_value(); this.removeFocus() };
			};
		};

	}

	parse_entered_value {
		var parsed_value;
		parsed_value = keystring.asFloat;
		keystring = "";
		parsed_value = parsed_value.clip(range[0], range[1]);
		value = parsed_value;
		bound_parameter.val_(value, report_to_ui: false);
		this.value_from_parameter(parsed_value);
	}

	giveFocus {
		has_focus = true;
		frame_view.drawFunc = {
			Pen.width = 0.5;
			Pen.color = Color.red;
			Pen.strokeRect(Rect(0, 0, bounds.width, bounds.height));
		};
		graphical_value = 0;
		view.refresh;
		value_display.stringColor = Color.red;
		frame_view.refresh;
	}

	removeFocus {
		has_focus = false;
		frame_view.drawFunc = {
			Pen.width = 0.5;
			Pen.color = Color.black;
			Pen.strokeRect(Rect(0, 0, bounds.width, bounds.height));
		};
		this.refresh_displayed_value;
		frame_view.refresh;
	}

	refresh_displayed_value {
		value_display.string = value.round(0.01);
		if(graphical_value > (range[1] / 2 + (range[1] / 10)), {
			value_display.stringColor = Color.white}, {
			value_display.stringColor = Color.black
		});
	}

	calculate_value {
		value = graphical_value.curvelin(range[0], range[1], range[0], range[1],
			curve_factor).round(0.01);
	}

	calculate_graphical_value {
		graphical_value = value.lincurve(range[0], range[1], range[0], range[1], curve_factor);
	}

	bind_to_parameter { |parameter|
		bound_parameter = parameter;
		type = bound_parameter.type;
		range = bound_parameter.range;
		default_value = bound_parameter.default;
		value = bound_parameter.val;
		parameter.bound_to_ui = true;
		parameter.ui = this;
		this.calculate_graphical_value;
	}

	value_from_parameter { |v|
		value = v;
		this.calculate_graphical_value();
		view.refresh;
		this.refresh_displayed_value();
	}


}