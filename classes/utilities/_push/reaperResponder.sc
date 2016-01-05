MGU_reaperResponder {

	var <target_osc_ip, <target_osc_port;
	var reaper;

	*new { |target_osc_ip, target_osc_port|
		^this.newCopyArgs(target_osc_ip, target_osc_port).init
	}

	init {
		reaper = NetAddr(target_osc_ip, target_osc_port);
	}

	// setters

	target_osc_ip_ { |ip|
		reaper.hostname = ip;
	}

	target_osc_port_ { |port|
		reaper.port = port;
	}

	// sending notes

	send_noteOn { |note, vel, chan|
		reaper.sendMsg("/vkb_midi/" ++ chan ++ "/note/" ++ note, vel);
	}

	send_noteOff { |note, chan|
		reaper.sendMsg("/vkb_midi/" ++ chan ++ "/note/" ++ note, 0);
	}

	// tracks

	select_track { |trackArray|
		if(trackArray.class != Array) { trackArray = [trackArray] };
		trackArray.do({|i|
			reaper.sendMsg("/track/" ++ trackArray[i] ++ "/select", 1)
		});
	}

	unselect_track { |trackArray|
				if(trackArray.class != Array) { trackArray = [trackArray] };
		trackArray.do({|i|
			reaper.sendMsg("/track/" ++ trackArray[i] ++ "/select", 0)
		});
	}

	arm_track { |trackArray|
		if(trackArray.class != Array) { trackArray = [trackArray] };
		trackArray.do({|i|
			reaper.sendMsg("/track/" ++ trackArray[i] ++ "/recarm", 1)
		});
	}

	unarm_track { |trackArray|
		if(trackArray.class != Array) { trackArray = [trackArray] };
		trackArray.do({|i|
			reaper.sendMsg("/track/" ++ trackArray[i] ++ "/recarm", 0)
		});
	}

	// transport

	play {
		reaper.sendMsg("/play", 1);
	}

	pause {
		reaper.sendMsg("/pause", 1);
	}

	stop {
		reaper.sendMsg("/stop", 1);
	}

	record { |value|
		reaper.sendMsg("/record", value);
	}

}