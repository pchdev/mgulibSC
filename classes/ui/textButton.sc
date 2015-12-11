MGU_textButton {

	var parent, bounds, bound_parameter;
	var view, displayed_text;

	*new {|parent, bounds, bound_parameter|
		^this.newCopyArgs(parent, bounds, bound_parameter).init
	}

	init {

		view = UserView(parent, bounds);


		view.drawFunc = {
			Pen.width = 0.5;
			Pen.fillColor = Color.white;
			Pen.fillRect(bounds);
		};

		view.mouseDownAction = {

		};

		view.mouseOverAction = {

		};

		displayed_text = StaticText(view, bounds);
		displayed_text.font = Font("Arial", 11);
		displayed_text.align = \topLeft;
		displayed_text.string = "try me";

	}

	setText { |string|
		displayed_text.string = string;
	}







}