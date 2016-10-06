MGU_moduleGUI {

	var module, address;
	var parameter_array, master_parameter_array;
	var description;
	var window, window_bounds;
	var ui_array;
	var title, description_text, type, main_menu;
	var vu_meter;
	var master_parameters_text;
	var user_parameters_text;

	*new {|module|
		^this.newCopyArgs(module).init
	}

	init {

		// THIS HAS TO BE REARRANGED WITH QT LAYOUTS - MUCH MORE ADAPTABLE THAT WAY

		// HEADER

		var wINDOW_SIZE = [640, 480];
		var tITLE_OFFSET = 15;
		var tITLE_SIZE = 18;
		var sUBTITLE_OFFSET = tITLE_OFFSET + tITLE_SIZE + 5;
		var sUBTITLE_SIZE = 11 * 2 ; // 2 lines for subtitles
		var hEADER_BUTTONS_OFFSET = sUBTITLE_OFFSET + sUBTITLE_SIZE + 10;
		var hEADER_BUTTONS_SIZE = [75, 25];
		var hEADER_SIZE = hEADER_BUTTONS_OFFSET + hEADER_BUTTONS_SIZE[1] + 20;
		var parameter_offset = 0;
		var container_collection = [];

		// MASTER PARAMETERS

		// query ui-relevant accesses from module & its container(s)
		parameter_array = module.container.parameter_array;
		master_parameter_array = module.master_container.parameter_array;
		address = module.container.address;

		// window
		window_bounds = Rect(0, 0, wINDOW_SIZE[0], wINDOW_SIZE[1]);
		window = Window(address, window_bounds, false, scroll: true);
		window.background = Color.white;

		// title
		title = StaticText(window, Rect(0, tITLE_OFFSET, wINDOW_SIZE[0] -2, tITLE_SIZE));
		title.font = Font("Arial", tITLE_SIZE, false);
		title.string = address + "module";
		title.align = \center;

		// subtitle - module description
		description = StaticText(window, Rect(0, sUBTITLE_OFFSET, wINDOW_SIZE[0] -2, sUBTITLE_SIZE));
		description.font = Font("Arial", sUBTITLE_SIZE/2);
		description.align = \center;

		module.description !? { description.string = module.description };
		module.description ?? { description.string = "no description currently available.." };

		// main menu

		main_menu = PopUpMenu(window, Rect(wINDOW_SIZE[0]/2 - 80, hEADER_BUTTONS_OFFSET, 160, 20))
		.items_(["send synth", "kill all synths", "bypass"])
		.background_(Color.white)
		.action_({|menu|
			switch(menu.item)
			{"send synth"} { module.sendSynth() }
			{"kill all synths"} { module.killAllSynths() }})
		.allowsReselection_(true)
		.font_(Font("Arial", 11));

		// preset menu

		// VU METERS

		// inputs

		if(module.type == \effect) {
			module.num_inputs.do({|i|
				var offset = module.num_inputs * 7;
				MGU_vuMeter(window, Rect(105 + (13*i) - offset, 20, 10, 80), address ++ "/input", i*2);
			});
		};

		// outputs

		module.num_outputs.do({|i|
			var offset = module.num_outputs * 7;
			vu_meter = MGU_vuMeter(window, Rect(535 + (13*i) - offset, 20, 10, 80), address, i*2);
		});

		// PARAMETERS

		// collecting containers

		module.instVarSize.do({|i|
			if(module.instVarAt(i).class == MGU_container)
			{ container_collection = container_collection.add(module.instVarAt(i)) };
		});

		if(module.class == MGU_moduleRack) { // if module rack
			module.module_array.do({|m|
				m.instVarSize.do({|i|
					if(m.instVarAt(i).class == MGU_container)
					{ container_collection = container_collection.add(m.instVarAt(i)) };
			})});
		};

		// adding successive ui parameter groups

		container_collection.do({|container|
			var offset = hEADER_BUTTONS_OFFSET + 20 + 15 + parameter_offset;
			var c = MGU_containerUI(container, window, Rect(20, offset, wINDOW_SIZE[0] - 2, 0));
			parameter_offset = parameter_offset + c.computeRequiredUISize();

		});

		// when closing window, unbind parameters from ui elements
		window.onClose = {
			parameter_array.do({|param|
				param.bound_to_ui = false;
				param.ui = nil;
			});
		};

		window.front;
	}

}