MGU_textButton {

	var parent, bounds, label, bound_action;
	var background_color;
	var frame_view, view, displayed_text;

	*new {|parent, bounds, label, bound_action|
		^this.newCopyArgs(parent, bounds, label, bound_action).init
	}

	init {

		label ?? { label = "nil" };
		view = UserView(parent, bounds);
		//background_color = Color.new255(115,150,171);
		background_color = Color.white;

		frame_view = UserView(parent, bounds);
		frame_view.acceptsMouseOver = true;
		view = UserView(frame_view, Rect(0, 0, bounds.width, bounds.height));

		frame_view.drawFunc = {
			Pen.width = 1;
			Pen.color = Color.black;
			Pen.strokeRect(Rect(0, 0, bounds.width, bounds.height));
		};

		view.drawFunc = {
				Pen.width = 0.5;
				Pen.fillColor = background_color;
			Pen.fillRect(Rect(1, 1, bounds.width - 2, bounds.height - 2));
		};

		view.mouseDownAction = {
			bound_action.value();
			background_color = Color.red(0.5);
			view.refresh();
			AppClock.sched(0.15, {background_color = Color.new255(115, 150,171); displayed_text.refresh(); view.refresh()});

		};

		frame_view.mouseEnterAction = {
			background_color = Color.new255(115,150,171);
			displayed_text.stringColor = Color.white;
			view.refresh();
			displayed_text.refresh();
		};

		frame_view.mouseLeaveAction = {
			background_color = Color.white;
			displayed_text.stringColor = Color.black;
			view.refresh();
			displayed_text.refresh();
		};

		displayed_text = StaticText(view, Rect(0, 0, bounds.width, bounds.height));
		displayed_text.font = Font("Arial", 9);
		displayed_text.align = \center;
		displayed_text.string = label;

	}

	setText { |string|
		displayed_text.string = string;
	}







}