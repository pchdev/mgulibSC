
MGU_mainToolPanel {

	var server, audioConfig, audioTester;
	var window, audioConfigUI, audioTesterUI;

	*new { |server|
		^this.newCopyArgs(server).init
	}

	init {
		audioConfig = MGU_audioConfig.new(server);
		audioTester = MGU_audioTester.new(server)
	}

	initUI {

		window = Window.new("main Tool panel", Rect(10, 10, 300, 300), false);
		window.front;

		audioConfigUI = Button.new(window, Rect(10, 10, 50, 20));
		audioConfigUI.states = [["show"]];
		audioConfigUI.action = {|val| audioConfig.initUI};
		StaticText.new(window, Rect(70, 10, 110, 20)).string_("audio configuration");

		audioTesterUI = Button.new(window, Rect(10, 35, 50, 20));
		audioTesterUI.states = [["show"]];
		audioTesterUI.action = {|val| audioTester.initUI};
		StaticText.new(window, Rect(70, 35, 110, 20)).string_("audio tester");

	}
}

	