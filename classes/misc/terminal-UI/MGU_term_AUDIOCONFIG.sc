MGU_term_AUDIOCONFIG {

	// instance variables
	var <server, parent, bounds;
	var <inDevices, <outDevices;
	var currentInDevice_index, currentOutDevice_index;
	var currentDevice_func;
	var mainView, inList, outList, inChans, outChans;
	var descText1, descText2, descText3, descText4;
	var <backgroundColor, <textColor, <font;

	*new { arg server = Server.default, parent, bounds;
		^this.newCopyArgs(server, parent, bounds).init
	}

	init {

		inDevices = ServerOptions.inDevices;
		outDevices = ServerOptions.outDevices;

		currentInDevice_index = { currentDevice_func.value(inDevices, server.options.inDevice)};
		currentOutDevice_index = { currentDevice_func.value(outDevices, server.options.outDevice)};

		currentDevice_func = { |deviceArray, currentDevice|
			var index = 0;
			deviceArray.size.do( {|i|
				if(currentDevice == deviceArray.at(i), {
					index = i})});
		index.postln};
	}

	initUI {

		mainView = UserView.new(parent, bounds);

		// INPUT : POPUP + TEXT

		inList = PopUpMenu.new(mainView, Rect(15, 15, 150, 20));
		inList.items = inDevices;
		inList.background = Color.blue;

		descText1 = StaticText.new(mainView, Rect(175, 15, 100, 20))
		.string_("input device");

		inList.action = {|val|
			if(server.serverRunning, { server.quit;
			server.options.inDevice = outList.items.at(val.value);
				server.boot}, {
				server.options.inDevice = inList.items.at(val.value);
					server.boot})};
		inList.value_(currentInDevice_index.value);

		// OUTPUT : POPUP + TEXT

		outList = PopUpMenu.new(mainView, Rect(15, 40, 150, 20));
		outList.items = outDevices;
		outList.background = Color.white;

		descText2 = StaticText.new(mainView, Rect(175, 40, 100, 20))
		.string_("output device");

		outList.action = {|val|
			if(server.serverRunning, { server.quit;
			server.options.outDevice = outList.items.at(val.value);
			server.boot}, {
					server.options.outDevice = outList.items.at(val.value);
					server.boot})};
		outList.value_(currentOutDevice_index.value);


		// INPUT CHANNELS N°

		inChans = NumberBox.new(mainView, Rect(15, 65, 45, 20));
		inChans.value = server.options.numInputBusChannels;
		inChans.action = {|val| server.options.numInputBusChannels = val.value};


		descText3 = StaticText.new(mainView, Rect(65, 65, 200, 20))
		.string_("n° of input channels");

		// OUTPUT CHANNELS N°

		outChans = NumberBox.new(mainView, Rect(15, 90, 45, 20));
		outChans.value = server.options.numOutputBusChannels;
		outChans.action = {|val| server.options.numOutputBusChannels = val.value};

		descText4 = StaticText.new(mainView, Rect(65, 90, 200, 20))
		.string_("n° of output channels");

	}

	// APPEARANCE MTHODS

	backgroundColor_ { |aColor|

		backgroundColor = aColor;
		mainView.background = backgroundColor;
		inList.background = backgroundColor;
		inList.refresh;
		outList.background = backgroundColor;
		inChans.background = backgroundColor;
		outChans.background = backgroundColor;

	}

	textColor_ { |aColor|
		textColor = aColor;
		inList.stringColor = textColor;
		outList.stringColor = textColor;
		inChans.stringColor = textColor;
		outChans.stringColor = textColor;

	}

	font_ { |aFont|
		font = aFont;
		inList.font = font;
		outList.font = font;
		inChans.font = font;
		outChans.font = font;
		descText1.font = font;
		descText2.font = font;
		descText3.font = font;
		descText4.font = font;
	}



}
