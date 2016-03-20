MGU_moduleGUI {

	var module, address;
	var parameter_array;
	var description;
	var window, window_bounds;
	var ui_array;
	var title, description_text, type, <sendsynth_button, <bypass_button;
	var vu_meter;

	*new {|module|
		^this.newCopyArgs(module).init
	}

	init {

		// query ui-relevant accesses from module & its container(s)
		parameter_array = module.container.paramAccesses;
		address = module.container.address;

		// window
		window_bounds = Rect(0, 0, 640, 480);
		window = Window(address, window_bounds, false, scroll: true);
		window.background = Color.white;

		window.drawFunc_({ // header separator
			Pen.width = 0.5;
			Pen.strokeColor = Color.black;
			Pen.line(Point(0, 115), Point(640, 115));
			Pen.stroke;
		});

		// title
		title = StaticText(window, Rect(0, 0, 640, 50));
		title.font = Font("Arial", 18, false);
		title.string = address + "module";
		title.align = \center;

		// subtitle - module description
		description = StaticText(window, Rect(0, 25, 640, 50));
		description.font = Font("Arial", 11);
		description.align = \center;

		module.description !? { description.string = module.description };
		module.description ?? { description.string = "no description currently available.." };

		// module buttons
		sendsynth_button = MGU_textToggle(window, Rect(window_bounds.width/2 - 75, 70, 75, 25),
			"send synth", "kill synth", [{module.killAllSynths()}, {module.sendSynth()}]);

		bypass_button = MGU_textToggle(window, Rect(window_bounds.width/2 -1, 70, 75, 25),
			"bypass off", "bypass on");

		// preset menu

		// vu meters
		module.numOutputs.do({|i|
			vu_meter = MGU_vuMeter(window, Rect(550 + (13*i), 20, 10, 80), address, i);
		});

		// parameters

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