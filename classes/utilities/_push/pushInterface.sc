MGU_pushInterface {

	var <target_osc_ip, <target_osc_port, <target_midi_device;
	var parser;
	var reaper_responder, push_responder;
	var reaper_device, push_device;

	*new { |target_osc_ip, target_osc_port = 8888|
		^this.newCopyArgs(target_osc_ip, target_osc_port).init;
	}

	init {

		target_osc_ip ?? { target_osc_ip = "127.0.0.1" };

		"initializing push-reaper interface".postln;

		MIDIClient.init;
		target_midi_device = MIDIClient.sources;
		target_midi_device.postln;
		target_midi_device.size.do({|i|
			if((target_midi_device[i].device == "Ableton Push")
				&& (target_midi_device[i].name == "User Port"), {
				target_midi_device = i })
		});

		MIDIIn.connectAll;

		reaper_responder = MGU_reaperResponder(target_osc_ip, target_osc_port);
		push_responder = MGU_pushResponder(target_midi_device);

		parser = MGU_pushParser(reaper_responder, push_responder);

		push_device = MGU_pushDevice(parser);
		reaper_device = MGU_reaperDevice(parser);

		// device only sends push information to the parser.
		// parser interprets and passes the information to the responder (reaper)
		// feedback is only done with sysex messaging, back to the device (push)

		/*

		- push_interface
		________________

		----- push_device -> MIDIFuncs responding to the device, transmitting to the parser
		      ----- push_toggle
		      ----- push_ccKnob
		      ----- push_padNote
		      ----- push_controlButton
		----- reaper_device -> OSCFuncs responding to the device, transmitting to the parser
		----- push_responder -> called to transmit values back to the push device
		----- reaper_responder -> called to transmit values to reaper
		----- push_parser -> manages all transiting information, calling responder classes when needed

		*/

	}

}