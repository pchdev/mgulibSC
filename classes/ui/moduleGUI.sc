MGU_moduleGUI {

	var name, parameter_array, alwaysOnTop, <description, <parent;
	var window, window_bounds;
	var ui_array;
	var title, description_text, type, sendsynth_button, bypass_button;
	var vu_meter;

	*new {|name, parameter_array, alwaysOnTop, description, parent|
		^this.newCopyArgs(name, parameter_array, alwaysOnTop, description, parent).init
	}

	init {

		window_bounds = Rect(0, 0, 640, 480);
		window = Window(name, window_bounds, false, scroll: true);
		window.background = Color.white;
		if(alwaysOnTop) {window.alwaysOnTop = true};

		title = StaticText(window, Rect(0, 0, 640, 50));
		title.font = Font("Arial", 18, false);
		title.string = name + "module";
		title.align = \center;

		description_text = StaticText(window, Rect(0, 25, 640, 50));
		description_text.font = Font("Arial", 11);
		description ?? { description = "no description currently available" };
		description_text.string = description;
		description_text.align = \center;

		sendsynth_button = MGU_textToggle(window, Rect(window_bounds.width/2 - 75, 70, 75, 25),
			"send synth", "kill synth", [{parent.killAllSynths()}, {parent.sendSynth()}]);

		bypass_button = MGU_textToggle(window, Rect(window_bounds.width/2 -1, 70, 75, 25),
			"bypass off", "bypass on");

		window.drawFunc_({
			Pen.width = 0.5;
			Pen.strokeColor = Color.black;
			Pen.line(Point(0, 115), Point(640, 115));
			Pen.stroke;
		});

		vu_meter = MGU_vuMeter(window, nil, name);

		parameter_array.size.do({|i|
			var parameter = parameter_array[i];
			var type = parameter_array[i].type;
			var pname = parameter_array[i].name;
			var range = parameter_array[i].range;
			var y_offset = 145 + (i*30);

			case

			{ type == Integer } {
				if(range == [0, 1]) { ui_array = ui_array.add(MGU_toggle(window,
					Rect(20, y_offset, 17, 17), parameter)) }
				{ if(pname != \inbuser) { ui_array = ui_array.add(MGU_slider(window,
					Rect(20, y_offset, 150, 20), parameter)) }}}
			{ type == Float } {
				if(pname == \freq) { ui_array.add(MGU_slider(window,
					Rect(20, y_offset, 150, 20), parameter, 7)) } {
					ui_array.add(MGU_slider(window, Rect(20, y_offset, 150, 20), parameter))};
			};
		});

		// when closing window, unbind parameters from ui elements
		window.onClose = {
			parameter_array.size.do({|i|
				parameter_array[i].bound_to_ui = false;
				parameter_array[i].ui = nil;
			});
		};

		window.front;
	}

}