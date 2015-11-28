MGU_slider {

	var parent, bounds, bound_parameter;
	var orientation, background_color;
	var frame_view, view, value_display, parameter_address_display;
	var type, range, value, default_value, graphical_value;
	var curve_factor;

	// implement reactive tooltip


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

		background_color = Color.new255(55,90,101);
		curve_factor = -6;
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

			// double click resets to default value
			if(cc == 2) { value = default_value; this.calculate_graphical_value };

			view.refresh;

			// update parameter value
			bound_parameter.val = value;

			// value string display
			this.refresh_displayed_value;
		};

		view.mouseDownAction = mouse_actions;
		view.mouseMoveAction = mouse_actions;

		// text display
		value_display = StaticText(frame_view, Rect(bounds.width/2 - 5, bounds.height/2 - 6,
			bounds.width/2, bounds.height/2));
		value_display.font = Font("Arial", 10);
		value_display.acceptsMouse = false;
		value_display.align = \topLeft;

		this.refresh_displayed_value();


		// parameter address display
		parameter_address_display = StaticText(parent, Rect(bounds.left + bounds.width + 15,
			bounds.top + (frame_view.bounds.height/4) - 1, bound_parameter.address.size * 11, 12));
		parameter_address_display.font = Font("Arial", 11);
		parameter_address_display.align = \topLeft;
		parameter_address_display.string = bound_parameter.address;

		parameter_address_display.mouseDownAction = { |me, x, y, mod, bn, cc|
			if(cc == 2) { value = default_value; this.calculate_graphical_value };
			view.refresh;
			this.refresh_displayed_value();
		};

	}

	refresh_displayed_value {
		value_display.string = value.round(0.01);
		if(graphical_value > (range[1] / 2 + (range[1] / 10)), {
			value_display.stringColor = Color.white}, {
			value_display.stringColor = Color.black
		});
	}

	calculate_value {
		value = graphical_value.curvelin(range[0], range[1], range[0], range[1], curve_factor);
	}

	calculate_graphical_value {
		graphical_value = value.curvelin(range[0], range[1], range[0], range[1], curve_factor.neg);
	}

	bind_to_parameter { |parameter|
		bound_parameter = parameter;
		type = bound_parameter.type;
		range = bound_parameter.range;
		default_value = bound_parameter.default;
		value = default_value;
		this.calculate_graphical_value;
	}

}