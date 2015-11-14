MGU_pushControlButton {

	var address, ccNum, netaddr, midiout, initState, type;
	var val;

	*new { |address, ccNum, netaddr, midiout, initState = 0, type = \toggle|
		^this.newCopyArgs(address, ccNum, netaddr, midiout, initState, type).init
	}

	init {

		val = 0;
		MIDIFunc.cc({|v, num|
			if(type == \toggle, { this.val_(v, true) }, {
				this.val_(v, false)})
		}, ccNum);
		if(initState == 1, { this.val_(127, true) });

	}

	val_ { |sentValue, out|

		var outFunc;

		outFunc = {
			if(out, {
				midiout.control(0, ccNum, val * 127)});
			["push", address, val].postln;
			netaddr !? { netaddr.sendBundle(nil, [address, val]) };
		};

		if(sentValue == 127, {
			if(type == \toggle, {
				if(val == 0, { val = 1 }, { val = 0 })}, {
				val = 1});
			outFunc.value; // filtering out repetitions
		});

	}

}