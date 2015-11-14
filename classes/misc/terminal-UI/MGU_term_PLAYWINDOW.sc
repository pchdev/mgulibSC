MGU_term_PLAYWINDOW {

	// instance variables

	var parent;
	var view_width, view_height;
	var <>mainView;
	var <backgroundColor, <textColor, <font;

	// class methods

	*new { |parent|
		^this.newCopyArgs(parent).init
	}

	// instance methods

	init {

		// general properties

		view_width = (parent.bounds.width / 8) * 5;
		view_height = parent.bounds.height / 2;

		// view properties

		mainView = UserView.new(parent, Rect(((parent.bounds.width / 8) * 3),
			view_height + 1, view_width, view_height - 25));

	}

	backgroundColor_ { |aColor|
		backgroundColor = aColor;
		mainView.background = backgroundColor;
	}

	textColor_ { |aColor|
		textColor = aColor;
	}

	font_ { |aFont|
		font = aFont;
	}


}
	