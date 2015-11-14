MGU_term_BUTTON {

	// attributes

	classvar instanceTotal;

	var <>parent, <>bounds, <>states, tabParent;
	var <instance;
	var mainView;
	var button_text;
	var button_state;
	var <backgroundColor, <textColor, <font;

	// class methods

	*new {|parent, bounds, states, tabParent = nil|
		^this.newCopyArgs(parent, bounds, states, tabParent).init
	}

	init {

		// instance count

		if(instanceTotal.isNil, {
			instanceTotal = 0}, { // else
				instanceTotal = instanceTotal + 1});

		instance = instanceTotal;
		("Button instance n°" + instance).postln;

		mainView = UserView.new(parent, bounds);
		button_state = 0;
		mainView.background = backgroundColor;

		mainView.drawFunc = {|thisview|
			Pen.width = 1;
			Pen.strokeColor = textColor;
			Pen.strokeRect(Rect(1, 1, bounds.width - 2, bounds.height - 2));

		};

		button_text = StaticText.new(mainView, Rect(1, 1, bounds.width - 2, bounds.height - 2));
		button_text.stringColor = textColor;
		button_text.string = states;
		button_text.align = \center;
		button_text.font = font;

		button_text.mouseOverAction = {
			if(button_state == 0, {
			mainView.drawFunc = {|thisview|
				Pen.width = 1;
				Pen.strokeColor = Color.new255(90, 106, 127);
				Pen.strokeRect(Rect(1, 1, bounds.width - 2, bounds.height - 2));
			};
			mainView.refresh});
			};

		button_text.mouseLeaveAction = {
			if(button_state == 0, {
			mainView.drawFunc = {|thisview|
				Pen.width = 1;
				Pen.strokeColor = textColor;
				Pen.strokeRect(Rect(1, 1, bounds.width - 2, bounds.height - 2));
			};
			mainView.refresh});
			};

		button_text.mouseDownAction = {
			if(button_state == 0, {
			this.selectButton;
			this.selectedButton;
			});
		};

	}

	// APPEARANCE MTHODS

	backgroundColor_ { |aColor|
		backgroundColor = aColor;
	}

	textColor_ { |aColor|
		textColor = aColor;
	}

	font_ { |aFont|
		font = aFont;
		button_text.font = font;
	}

	// BUTTON SELECTION

	selectButton {

		mainView.drawFunc = {|thisview|
			Pen.width = 1;
			Pen.strokeColor = Color.new255(130, 154, 184);
			Pen.strokeRect(Rect(1, 1, bounds.width - 2, bounds.height - 2));
		};
		mainView.refresh;
		button_state = 1;
	}

	unselectButton {

		mainView.drawFunc = {|thisview|
			Pen.width = 1;
			Pen.strokeColor = textColor;
			Pen.strokeRect(Rect(1, 1, bounds.width - 2, bounds.height - 2))
		};

		mainView.refresh;
		button_state = 0;
	}

	selectedButton { // call to utility window
		tabParent.tabCall(instance);
	}

	resetInstanceTotal {
		instanceTotal = nil
	}

}	