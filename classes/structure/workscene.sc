MGU_workScene {

	var <name;
	var <module_array;
	var <minuit_interface;
	var <gui;

	*new {|name|
		^this.newCopyArgs(name).init
	}

	init {

		name ?? { name = "Untitled work scene" };

	}

	generateUI {
		gui = MGU_workSceneUI(this, Rect(0, 0, 1200, 700));
	}

}