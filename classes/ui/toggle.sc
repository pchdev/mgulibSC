MGU_toggle {

	var parent, bounds;
	var toggleView;

	*new { |parent, bounds|
		^this.newCopyArgs(parent, bounds).init
	}

	init {

		bounds ?? { bounds = Rect(0, 0, 50, 50) };
		toggleView = UserView(parent, bounds);


	}



}