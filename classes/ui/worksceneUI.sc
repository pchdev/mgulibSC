MGU_workSceneUI {

	var workscene_target, bounds;
	var window;
	var control_panel, minuit_panel;

	var header, subtitle_description;
	var header_separator;

	*new {|workscene_target, bounds|
		^this.newCopyArgs(workscene_target, bounds).init
	}

	init {

		window = Window(workscene_target.name, bounds, false)
		.background = Color.white;

		header_separator = MGU_hSeparator(window, Rect(0, 75, bounds.width, 1));
		MGU_hSeparator(window, Rect(0, bounds.height - 75, bounds.width, 1));
		MGU_vSeparator(window, Rect(bounds.width/3 * 1.75, 75, 1, bounds.height - 150));

		// header

		StaticText(window, Rect(0, 0, bounds.width -2, 45))
		.font_(Font("Arial", 10))
		.string_(workscene_target.name)
		.align_(\center);


		window.front();


	}

}