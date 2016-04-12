MGU_containerUI {

	var container_source, parent, bounds;
	var separator, header;
	var ui_array;
	var param_array;
	var view;

	*new { |container_source, parent, bounds|
		^this.newCopyArgs(container_source, parent, bounds).init
	}

	init {

		var sPACING = 10, required_size;
		required_size = this.computeRequiredUISize();

		view = UserView(parent, Rect(bounds.left, bounds.top + 5, bounds.width - 20, required_size));

		ui_array = [];
		param_array = container_source.getDirectParameters();

		separator = MGU_hSeparator(view, Rect(0, 1, bounds.width - 20, 1));

		header = StaticText(view, Rect(0, sPACING, bounds.width, 10));
		header.font = Font("Arial", 10, false, false);

		if((container_source.parentContainer.notNil)
			&& (container_source.parentContainer.class != MGU_minuitInterface)) {
			header.string = container_source.parentContainer.name.toUpper
			+ container_source.name.toUpper + "PARAMETERS" } {
		header.string = container_source.name.toUpper + "PARAMETERS";
		};

		param_array.do({|param, i|

			var y_offset = 27 + (i*30);
			var slider_color;

				if(i%2 == 0)

				{ slider_color = MGU_colorPalette.blueGreenGrey() }
				{ slider_color = MGU_colorPalette.blueGreenGrey_2() };

				case

				{ (param.type == Integer) && (param.range == [0, 1]) }
				{ ui_array = ui_array.add(MGU_toggle(view, Rect(0, y_offset, 17, 17), param)) }

				{ ((param.type == Integer) || (param.type == Float)) && (param.name == \freq) }
				{ ui_array = ui_array.add(MGU_slider(view, Rect(0, y_offset, 150, 20), param, 7)
					.background_color_(slider_color)) }

				{ ((param.type == Integer) || (param.type == Float)) && (param.name != \freq) }
				{ ui_array = ui_array.add(MGU_slider(view, Rect(0, y_offset, 150, 20), param)
					.background_color_(slider_color)) }

				{ (param.type == Symbol) || (param.type == String) }
				{ ui_array = ui_array.add(
					PopUpMenu(view, Rect(0, y_offset, 150, 20))
					.items_(param.range)
					.background_(Color.white)

					.font_(Font("Arial", 11)))};

		});
	}

	computeRequiredUISize {

		// size =
		// header (10) + spacing (10) + spacing (5)
		// and spacing of 10 between each parameter

		var size = 27;
		var param_array = container_source.getDirectParameters();

		param_array.do({|param, i|

			case

			{ (param.type == Integer) && (param.range == [0, 1]) }
			{ size = size + 17 + 10 }

			{ ((((param.type == Integer) || (param.type == Float))) || (param.type == Symbol)) ||
				(param.type == String) }
			{ size = size + 20 + 10 }

		});

		^size
	}

}

