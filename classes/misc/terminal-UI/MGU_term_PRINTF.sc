MGU_term_PRINTF {

	// instance variables

	var parent, debugMode, <>scanf_module, <>parser_module, <>audio_module;
	var view_width, view_height;
	var mainView;
	var printf, printf_text;
	var printf_defileSpeed;
	var <backgroundColor, <textColor, <font;
	var username;
	var qList, qPosition, postBusyState;

	// class methods

	*new { |parent, debugMode = false|
		^this.newCopyArgs(parent, debugMode) // init call in main
	}

	// instance methods

	init {

		// general properties

		view_width = parent.bounds.width / 2 -5;
		view_height = parent.bounds.height / 2;
		printf_defileSpeed = 0.025;

		// view properties

		mainView = UserView.new(parent, Rect(view_width + 6, 0, view_width + 5, view_height));
		mainView.canFocus = false;

		// cue list

		qList = Array.new;
		qPosition = 0;
		postBusyState = false;

		// PRINTF TEXT

		printf = StaticText.new(mainView, Rect(10, 10, view_width - 30, view_height - 20));
		printf.align = \topLeft;

		if(debugMode, {
			username = "DEBUG";
			this.startView("DEBUGMODE")
			}, {
				this.welcomeMsg});

	}

	// APPEARANCE

	backgroundColor_ { |aColor|
		backgroundColor = aColor;
		mainView.background = backgroundColor;
	}

	textColor_ { |aColor|
		textColor = aColor;
		printf.stringColor = textColor;
	}

	font_ { |aFont|
		font = aFont;
		printf.font = font;
	}

	// CUE LIST

	addToCue { |stringToBePosted, doneAction, waitValue = 0, defileSpeed = 0.025|
		if(postBusyState, { // if busy, waits for the current post to be completed.
			postBusyState.postln;
			qList = qList.add({this.post(stringToBePosted, doneAction, waitValue, defileSpeed)})}, {
				postBusyState.postln;
				this.post(stringToBePosted, doneAction, waitValue, defileSpeed);
				// ^ else, posts immediately
		});

	}

	nextInCue {
		// checking for next cues
		if(qList.size > qPosition, { // if next cues available
			qList.at(qPosition).value; // do this
			qPosition = qPosition +1}); // else... do nothing!
	}

	// POST/ERASE & ANIMATION RELATED

	post { |string, doneAction, waitValue, defileSpeed|

		var stringSize = string.ascii.size;
		var routine;
		doneAction.postln;

		routine = Routine.new({
			stringSize.do({|i| // posting action...
				postBusyState = true;
				printf_text = printf_text ++ string.at(i);
				printf.string = printf_text;
				defileSpeed.yield;

				if(i == (stringSize - 1), { // posting completed
					waitValue.yield;
					doneAction.value;
					postBusyState = false;
					this.nextInCue}); // checks for next posting tasks (callback of sorts...)
			})});

		AppClock.play(routine)

		}

	clearView { |doneAction, waitValue = 0, defileSpeed = 0.0125|

		var stringSize = printf_text.ascii.size;
		var routine;
		printf_text.postln;
		routine = Routine.new({
			stringSize.do({|i|
				printf_text.pop;
				printf.string = printf_text;
				defileSpeed.yield;

				if(i == (stringSize - 1), {
					waitValue.yield;
					doneAction.value})
				})
			});

		AppClock.play(routine);
	}

	eraseLine {|nbOfLines|

	}



	// LOGIN RELATED

	welcomeMsg {

		this.post("WELCOME TO THE TERMINAL, PLEASE ENTER USER LOGIN...\n---------------------------------------------------", {
			parser_module.loginState_(1) });

	}

	checkPassword {
		this.post("\n\nPLEASE ENTER PASSWORD", {
			parser_module.loginState_(0);
			parser_module.passwordState_(1)
		})
	}

	// auth success sequence

	authSuccess {|user|
		this.post("\n\nUSER AUTHENTIFICATION COMPLETED", {this.accessGranted(user)}, 0.06)
	}

	accessGranted {|user|
		this.post("\nACCESS GRANTED", {this.welcomeUser(user)}, 0.06);
		username = user;
	}

	welcomeUser {|user|
		this.post("\nWELCOME TO THE TERMINAL," + user ++ "!", {
			parser_module.passwordState_(0);
			this.clearView({this.startView}, 1)}, 1.3, 0.05)
	}

	// auth failed sequence

	authFail {|user|
		this.post("\n\nUSER AUTHENTIFICATION FAILED", { this.accessDenied(user) }, 1);
	}

	accessDenied {|user|
		this.post("\nACCESS DENIED", { this.fuckUser(user) }, 1, 0.05);
	}

	fuckUser {|user|
		this.post("\nFUCK YOU \"" ++ user ++ "\"... OR WHOEVER THE HELL YOU CLAIM YOU ARE...", defileSpeed: 0.075);
	}

	// PLAY MODE RELATED

	startView {|user = "ADMIN"|
		this.addToCue("USER:" + username + "// TERMINAL SESSION\n-------------------------------", {
				parser_module.playState_(1)}, defileSpeed: 0.05);
	}


	// SERVER RELATED POSTS

	serverBooting {
		this.addToCue("\n\nNOW ATTEMPTING TO BOOT SERVER...", { audio_module.checkServer }, 1.0);
	}

	serverBootFailed {
		this.addToCue("\n\nSERVER BOOT ATTEMPT FAILED, PLEASE CHECK FOR ERRORS");
	}

	stopServer {
		this.addToCue("\n\nNOW ATTEMPTING TO STOP SERVER...");
	}

	serverActive {
		this.addToCue("\n\nSERVER IS CURRENTLY RUNNING");
	}

	serverInactive {
		this.addToCue("\n\nSERVER IS CURRENTLY INACTIVE");
	}

}

