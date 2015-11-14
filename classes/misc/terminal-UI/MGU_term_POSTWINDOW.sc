MGU_term_POSTWINDOW {

	// instance variables

	var parent;
	var mainView;
	var view_width, view_height;
	var text, currentLine;
	var mainLog;
	var oscReceiver;
	var lineCount;
	var textLog;
	var <backgroundColor, <textColor, <font;

	// class methods

	*new { |parent|
		^this.newCopyArgs(parent).init
	}

	// instance methods

	init {

		// general properties

		view_width = parent.window.bounds.width / 2;
		view_height = parent.window.bounds.height / 4;

		// view properties

		mainView = ScrollView.new(parent.window, Rect(0, view_height + 1, view_width, view_height - 1));
		mainView.autohidesScrollers = true;
		mainView.hasVerticalScroller = false;
		mainView.canFocus = false;
		mainView.hasBorder = false;

		// POST TEXT

		text = StaticText.new(mainView, Rect(10, 10, view_width - 30, view_height - 20));
		text.align = \topLeft;
		text.string = \;
		textLog = \;

		lineCount = 0;

		// OSC RECEPTION

		oscReceiver = OSCFunc.new({|msg, time, addr, recvPort|
			var processedLine;
			lineCount = lineCount + 1;
			currentLine = msg.at(1).asString.quote ++ "\n";
			mainLog = mainLog ++ currentLine;
			processedLine = "> OSC.msg rcvd on port:" + recvPort + "at adress:"
			+ msg.at(0) + ">" + currentLine;
			this.post(processedLine);
		}, '/term', nil, 3127);

		//midiReceiver = MIDIIn

	}

	post { |string|
		var routine;
		lineCount = lineCount + 1;
		routine = Routine.new({
			text.string = text.string ++ string;

			if(lineCount >= 14, {
				text.resizeTo(view_width - 30, (lineCount * 11) + 15);
				mainView.visibleOrigin = 0@((lineCount * 11))});
		});

		AppClock.play(routine);
	}


	backgroundColor_ { |aColor|
		backgroundColor = aColor;
		mainView.background = backgroundColor;
	}

	textColor_ { |aColor|
		textColor = aColor;
		text.stringColor = textColor;
	}

	font_ { |aFont|
		font = aFont;
		text.font = font;
	}

}