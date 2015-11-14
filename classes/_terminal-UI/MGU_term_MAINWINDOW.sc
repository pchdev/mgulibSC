MGU_term_MAINWINDOW {

	// instance variables
	var server, debugMode;
	var <>window_width, <>window_height;
	var <window;
	var scanf_module, <printf_module, parser_module, audio_module, post_module;
	var utility_module, play_module;
	var <moduleBackgroundColor, <moduleTextColor, <moduleDefaultFont;

	// class methods

	*new {|server, debugMode = false|
		^this.newCopyArgs(server, debugMode).init
	}

	init {

		// main window

		window_width = 1024;
		window_height = 768;

		window = Window.new("Terminal main window", Rect(0, 0, window_width, window_height));
		window.alwaysOnTop = true;
		window.front;
		window.background = Color.black;
		window.acceptsMouseOver = true;

		// scanf top-left module
		scanf_module = MGU_term_SCANF.new(window);

		// postwindow left module
		post_module = MGU_term_POSTWINDOW.new(this);

		// utility bottom-left module
		utility_module = MGU_term_UTILITYWINDOW.new(window, server);

		// printf top-right module
		printf_module = MGU_term_PRINTF.new(window, debugMode);

		// playground bottom-right module
		play_module = MGU_term_PLAYWINDOW.new(window);

		// parser module
		parser_module = MGU_term_PARSER.new;

		// audio module
		audio_module = MGU_term_AUDIOMODULE.new(server);


		// linking modules
		parser_module.printf_module = printf_module;
		parser_module.scanf_module = scanf_module;
		parser_module.audio_module = audio_module;
		parser_module.post_module = post_module;

		audio_module.parser_module = parser_module;
		audio_module.printf_module = printf_module;
		audio_module.post_module = post_module;

		scanf_module.printf_module = printf_module;
		scanf_module.parser_module = parser_module;

		printf_module.scanf_module = scanf_module;
		printf_module.parser_module = parser_module;
		printf_module.audio_module = audio_module;

		// init modules
		scanf_module.init;
		printf_module.init;

		// default module color set
		this.moduleBackgroundColor = Color.white;
		this.moduleTextColor = Color.black;
		this.moduleDefaultFont = Font("Courier", 11);

	}

	// init default color set

	moduleBackgroundColor_ { |aColor|

		moduleBackgroundColor = aColor;
		post_module.backgroundColor = moduleBackgroundColor;
		printf_module.backgroundColor = moduleBackgroundColor;
		scanf_module.backgroundColor = moduleBackgroundColor;
		play_module.backgroundColor = moduleBackgroundColor;
		utility_module.backgroundColor = moduleBackgroundColor;
	}

	moduleTextColor_ { |aColor|
		moduleTextColor = aColor;
		post_module.textColor = moduleTextColor;
		printf_module.textColor = moduleTextColor;
		scanf_module.textColor = moduleTextColor;
		play_module.textColor = moduleTextColor;
		utility_module.textColor = moduleTextColor;
	}

	moduleDefaultFont_ { |aFont|
		moduleDefaultFont = aFont;
		post_module.font = moduleDefaultFont;
		printf_module.font = moduleDefaultFont;
		scanf_module.font = moduleDefaultFont;
		play_module.font = moduleDefaultFont;
		utility_module.font = moduleDefaultFont;
	}




}

