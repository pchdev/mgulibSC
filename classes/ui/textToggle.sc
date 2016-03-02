MGU_textToggle {

	var parent, bounds, state1, state2, bound_action;
	var state1_color, state2_color;
	var frame_view, view, displayed_text;
	var current_state, current_color;
	var state1_stringColor, state2_stringColor;
	var mouseOver_color, mouseOver_stringColor;

	*new {|parent, bounds, state1, state2, bound_action|
		^this.newCopyArgs(parent, bounds, state1, state2, bound_action).init
	}

	init {

		// colors

		state1_color = Color.white;
		state2_color = Color.black;
		state1_stringColor = Color.black;
		state2_stringColor = Color.white;

		mouseOver_color = Color.grey;
		mouseOver_stringColor = Color.black;

		current_color = state1_color;

		// init

		current_state = 0;
		current_color = state1_color;

		view = UserView(parent, bounds);
		state1 ?? { state1 = "nil" };
		state2 ?? { state2 = "nil" };


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
			Pen.fillColor = current_color;
			Pen.fillRect(Rect(1, 1, bounds.width - 2, bounds.height - 2));
		};

		view.mouseDownAction = {
			if(current_state == 0) { this.current_state_(1)} { this.current_state_(0) };
			bound_action[current_state].value();
			view.refresh();
		};

		frame_view.mouseEnterAction = {
			current_color = mouseOver_color;
			displayed_text.stringColor = mouseOver_stringColor;
			view.refresh();
			displayed_text.refresh();
		};

		frame_view.mouseLeaveAction = {
			this.current_state_(current_state);
		};

		displayed_text = StaticText(view, Rect(0, 0, bounds.width, bounds.height));
		displayed_text.stringColor = state1_stringColor;
		displayed_text.font = Font("Arial", 9);
		displayed_text.align = \center;
		displayed_text.string = state1;

	}

	setText { |string|
		displayed_text.string = string;
	}

	current_state_ { |v|
		current_state = v;
		if(v == 0) {
			current_color = state1_color;
			displayed_text.stringColor = state1_stringColor;
			displayed_text.string = state1;
		} { // else
			current_color = state2_color;
			displayed_text.stringColor = state2_stringColor;
			displayed_text.string = state2;
		};

		view.refresh();
		displayed_text.refresh();

	}


}