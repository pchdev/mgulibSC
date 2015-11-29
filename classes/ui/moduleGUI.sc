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
		window = Window(name, window_bounds, false);
		window.background = Color.white;
		title = StaticText(window, Rect(0, 0, 640, 50));
		title.font = Font("Arial", 18, false);
		title.string = name + "module";
		title.align = \center;

		parameter_array.size.do({|i|
			if((parameter_array[i].type == Integer) || (parameter_array[i].type == Float)) {
				if(parameter_array[i].name != \inbus) {
					ui_array = ui_array.add(MGU_slider(window, Rect(20, 100 + (i*30), 150, 20),
						parameter_array[i]))
				};
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