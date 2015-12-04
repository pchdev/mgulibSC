MGU_moduleGUI {

	var name, parameter_array;
	var window, window_bounds;
	var ui_array;
	var title;

	*new {|name, parameter_array|
		^this.newCopyArgs(name, parameter_array).init
	}

	init {

		window_bounds = Rect(0, 0, 640, 480);
		window = Window(name, window_bounds, false, scroll: true);
		window.background = Color.white;
		title = StaticText(window, Rect(0, 0, 640, 50));
		title.font = Font("Arial", 18, false);
		title.string = name + "module";
		title.align = \center;

		parameter_array.size.do({|i|
			var parameter = parameter_array[i];
			var type = parameter_array[i].type;
			var pname = parameter_array[i].name;
			var range = parameter_array[i].range;
			var y_offset = 100 + (i*30);

			case

			{ type == Integer } {
				if(range == [0, 1]) { ui_array = ui_array.add(MGU_toggle(window,
					Rect(20, y_offset, 17, 17), parameter)) }
				{ if(pname != \inbus) { ui_array = ui_array.add(MGU_slider(window,
					Rect(20, y_offset, 150, 20), parameter)) }}}
			{ type == Float } {
				ui_array =
				if(pname == \gain) { ui_array.add(MGU_slider(window,
					Rect(20, y_offset, 150, 20), parameter, 5)) } {
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