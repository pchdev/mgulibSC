MGU_audioConfig {

	// instance variables
	var <server, <inDevices, <outDevices;
	var currentInDevice_index, currentOutDevice_index;
	var currentDevice_func;
	var window, inList, outList, inChans, outChans;

	*new { arg server = Server.default;
		^super.newCopyArgs(server).init
	}

	init {

		inDevices = ServerOptions.inDevices;
		outDevices = ServerOptions.outDevices;

		currentInDevice_index = { currentDevice_func.value(inDevices, server.options.inDevice)};
		currentOutDevice_index = { currentDevice_func.value(outDevices, server.options.outDevice)};

		currentDevice_func = { |deviceArray, currentDevice|
			var index;
			deviceArray.size.do( {|i|
				if(currentDevice == deviceArray.at(i), {
					index = i})});
		index};
	}

	initUI {

		window = Window.new("audioConfig", Rect(10, 10, 300, 300), false);
		window.front;

		inList = PopUpMenu.new(window, Rect(15, 15, 150, 60));
		inList.items = inDevices;
		StaticText.new(window, Rect(175, 35, 100, 30)).string_("INPUT DEVICE");
		inList.action = {|val|
			if(server.serverRunning, { server.quit;
			server.options.inDevice = outList.items.at(val.value);
				server.boot}, {
				server.options.inDevice = inList.items.at(val.value);
					server.boot})};
		inList.value_(currentInDevice_index.value);

		outList = PopUpMenu.new(window, Rect(15, 80, 150, 60));
		outList.items = outDevices;
		StaticText.new(window, Rect(175, 100, 100, 30)).string_("OUTPUT DEVICE");
		outList.action = {|val|
			if(server.serverRunning, { server.quit;
			server.options.outDevice = outList.items.at(val.value);
			server.boot}, {
					server.options.outDevice = outList.items.at(val.value);
					server.boot})};
		outList.value_(currentOutDevice_index.value);

		inChans = NumberBox.new(window, Rect(15, 145, 45, 20));
		inChans.value = server.options.numInputBusChannels;
		inChans.action = {|val| server.options.numInputBusChannels = val.value};
		StaticText.new(window, Rect(65, 145, 200, 20)).string_("n° of input channels");

		outChans = NumberBox.new(window, Rect(15, 170, 45, 20));
		outChans.value = server.options.numOutputBusChannels;
		outChans.action = {|val| server.options.numOutputBusChannels = val.value};
		StaticText.new(window, Rect(65, 170, 200, 20)).string_("n° of output channels");

	}



}







		