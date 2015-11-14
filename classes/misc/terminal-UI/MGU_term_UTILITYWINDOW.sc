MGU_term_UTILITYWINDOW {

	// instance var

	var parent, server;
	var view_width, view_height;
	var mainView;
	var tabButton_1, tabButton_2, tabButton_3, tabButton_4;
	var audioConfig;
	var <backgroundColor, <textColor, <font;

	// class methods

	*new { |parent, server|
		^this.newCopyArgs(parent).init
	}

	init {

		parent.onClose = { tabButton_1.resetInstanceTotal };

		// general properties

		view_width = ((parent.bounds.width / 8) * 3) - 1;
		view_height = parent.bounds.height / 2;

		// view properties

		mainView = UserView.new(parent, Rect(0, view_height + 1, view_width, view_height - 25));

		// tab buttons

		tabButton_1 = MGU_term_BUTTON.new(mainView, Rect(17, 10, 80, 20), "config", this);
		tabButton_2 = MGU_term_BUTTON.new(mainView, Rect(102, 10, 80, 20), "tree", this);
		tabButton_3 = MGU_term_BUTTON.new(mainView, Rect(187, 10, 80, 20), "monitor", this);
		tabButton_4 = MGU_term_BUTTON.new(mainView, Rect(272, 10, 80, 20), "browser", this);

		// audio config view

		audioConfig = MGU_term_AUDIOCONFIG.new(server, mainView, Rect(17, 65, 270, 200));
		audioConfig.initUI;

		// node tree view

		// monitor view

		// browser view

	}

	backgroundColor_ { |aColor|
		backgroundColor = aColor;
		mainView.background = backgroundColor;
		audioConfig.backgroundColor = backgroundColor; // passing to its children views
		tabButton_1.backgroundColor = backgroundColor;
		tabButton_2.backgroundColor = backgroundColor;
		tabButton_3.backgroundColor = backgroundColor;
		tabButton_4.backgroundColor = backgroundColor;
	}

	textColor_ { |aColor|
		textColor = aColor;
		audioConfig.textColor = textColor;
		tabButton_1.textColor = textColor;
		tabButton_2.textColor = textColor;
		tabButton_3.textColor = textColor;
		tabButton_4.textColor = textColor;
	}

	font_ { |aFont|
		font = aFont;
		audioConfig.font = font;
		tabButton_1.font = font;
		tabButton_2.font = font;
		tabButton_3.font = font;
		tabButton_4.font = font;
	}

	tabCall { |instance|

		switch (instance,

			0, { // audio config view
				tabButton_2.unselectButton;
				tabButton_3.unselectButton;
				tabButton_4.unselectButton;
			this.configView },

			1, {  // node tree view
				tabButton_1.unselectButton;
				tabButton_3.unselectButton;
				tabButton_4.unselectButton;
			this.treeView },

			2, { // monitoring view
				tabButton_1.unselectButton;
				tabButton_2.unselectButton;
				tabButton_4.unselectButton;
			this.monitorView },

			3, { // browser view
				tabButton_1.unselectButton;
				tabButton_2.unselectButton;
				tabButton_3.unselectButton;
			this.browserView }
		);

	}

	configView {
	}

	treeView {
	}

	monitorView {
	}

	browserView {
	}

}