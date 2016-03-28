MGU_moduleGUI {

	var module, address;
	var parameter_array;
	var description;
	var window, window_bounds;
	var ui_array;
	var title, description_text, type, <sendsynth_button, <bypass_button;
	var vu_meter;
	var master_parameters_text;

	*new {|module|
		^this.newCopyArgs(module).init
	}

	init {

		// HEADER

		var wINDOW_SIZE = [640, 480];
		var tITLE_OFFSET = 15;
		var tITLE_SIZE = 18;
		var sUBTITLE_OFFSET = tITLE_OFFSET + tITLE_SIZE + 5;
		var sUBTITLE_SIZE = 11 * 2 ; // 2 lines for subtitles
		var hEADER_BUTTONS_OFFSET = sUBTITLE_OFFSET + sUBTITLE_SIZE + 10;
		var hEADER_BUTTONS_SIZE = [75, 25];
		var hEADER_SIZE = hEADER_BUTTONS_OFFSET + hEADER_BUTTONS_SIZE[1] + 20;

		// MASTER PARAMETERS

		// query ui-relevant accesses from module & its container(s)
		parameter_array = module.container.parameter_array;
		address = module.container.address;

		// window
		window_bounds = Rect(0, 0, wINDOW_SIZE[0], wINDOW_SIZE[1]);
		window = Window(address, window_bounds, false, scroll: true);
		window.background = Color.white;

		window.drawFunc_({ // header separator
			Pen.width = 0.5;
			Pen.strokeColor = Color.black;
			Pen.line(Point(0, hEADER_SIZE), Point(wINDOW_SIZE[0], hEADER_SIZE));
			Pen.stroke;
		});

		// title
		title = StaticText(window, Rect(0, tITLE_OFFSET, wINDOW_SIZE[0], tITLE_SIZE));
		title.font = Font("Arial", tITLE_SIZE, false);
		title.string = address + "module";
		title.align = \center;

		// subtitle - module description
		description = StaticText(window, Rect(0, sUBTITLE_OFFSET, wINDOW_SIZE[0], sUBTITLE_SIZE));
		description.font = Font("Arial", sUBTITLE_SIZE/2);
		description.align = \center;

		module.description !? { description.string = module.description };
		module.description ?? { description.string = "no description currently available.." };

		// module buttons
		sendsynth_button = MGU_textToggle(window, Rect(
			window_bounds.width/2 - 75,
			hEADER_BUTTONS_OFFSET, 75, 25),
			"send synth", "kill synth", [{module.killAllSynths()}, {module.sendSynth()}]);

		bypass_button = MGU_textToggle(window, Rect( // tbi in core
			window_bounds.width/2 -1,
			hEADER_BUTTONS_OFFSET, 75, 25),
			"bypass off", "bypass on");

		// preset menu

		// vu meters
		module.num_outputs.do({|i|
			var offset = module.num_outputs * 7;
			vu_meter = MGU_vuMeter(window, Rect(535 + (13*i) - offset, 20, 10, 80), address, i*2);
		});

		master_parameters_text = StaticText(window, Rect(20, 125, 200, 15));
		master_parameters_text.font = Font("Arial", 10, false, false);
		master_parameters_text.string = "MASTER PARAMETERS";

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