MGU_term_SCANF {

	// instance variables

	var parent, <>printf_module, <>parser_module;
	var view_width, view_height;
	var mainView;
	var scanf_text, scanf, scanf_currentLine, scanf_enteredLine;
	var lineCount;
	var <>cryptedText;
	var <backgroundColor, <textColor, <font;

	// class methods

	*new { |parent|
		^this.newCopyArgs(parent) // init call in main
	}

	// instance methods

	init {

		// general properties

		view_width = parent.bounds.width / 2;
		view_height = parent.bounds.height / 4;

		// view properties

		mainView = ScrollView.new(parent, Rect(0, 0, view_width, view_height));
		mainView.hasBorder = false;
		mainView.autohidesScrollers = true;
		//mainView.hasHorizontalScroller = true;
		mainView.hasVerticalScroller = false;

		// SCANF TEXT

		scanf = StaticText.new(mainView, Rect(10, 10, view_width - 30, view_height - 20));
		scanf.align = \topLeft;
		scanf.string = "> ";
		scanf_currentLine = "> ";
		cryptedText = false;

		// MISC

		lineCount = 0;

		// view actions

		mainView.keyDownAction = { arg view, char, modifiers, unicode, keycode, key;

			// if char is alpha-numeric or punctuation,

			if (char.isAlphaNum || char.isPunct, {
				if(cryptedText, {
					scanf.string = scanf.string ++ "*"}, { // if crypted
						scanf.string = scanf.string ++ char.toUpper}); // add char to text
				scanf_currentLine = scanf_currentLine ++ char.toUpper}); // add char to line

			// if space key

			if (keycode == 49, {
				scanf.string = scanf.string ++ ($ ); // add space to text
				scanf_currentLine = scanf_currentLine ++ char.toUpper;}); // add space to line

			// if enter key

			if (keycode == 36, {
				lineCount = lineCount + 1;
				scanf_enteredLine = scanf_currentLine; // saving previous line
				scanf_currentLine = "> "; // clearing current Line contents
				scanf_enteredLine.postln; // posting entered line
				scanf.string = scanf.string ++ ($\n) ++ "> "; // adding \n & > to text

				if(lineCount >= 14, {
				scanf.resizeTo(view_width - 30, (lineCount * 11) + 27);
				mainView.visibleOrigin = 0@((lineCount * 11) + 27)});

				parser_module.parse(scanf_enteredLine);

				});


			// if <- key

			if (keycode == 51, {
				var lineSize = scanf_currentLine.ascii.size -1;
				var newString = scanf.string;

				if(lineSize >= 2, { // to avoid erasing the beginning "> "
					newString = scanf.string;
					newString.pop;
					scanf_currentLine.pop;
					scanf.string = newString;
			})});


		};

	}

	// APPEARANCE

	backgroundColor_ { |aColor|
		backgroundColor = aColor;
		mainView.background = backgroundColor;
	}

	textColor_ { |aColor|
		textColor = aColor;
		scanf.stringColor = textColor;
	}

	font_ { |aFont|
		font = aFont;
		scanf.font = font;
	}


}

