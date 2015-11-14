MGU_pushNote {

	var address, noteNb, netaddr, midiout, initState, type, color;
	var vel, state, borderArray, offColor;

	*new {|address, noteNb, netaddr, midiout, initState = 0, type, color|
		^this.newCopyArgs(address, noteNb, netaddr, midiout, initState, type, color).init
	}

	init {

		vel = 0;
		state = 0;

		MIDIFunc.noteOn({|vel, note|
			["push", address, "noteOn", vel].postln;
			this.valueNote(note, vel);
		}, noteNb);
		MIDIFunc.noteOff({|vel, note|
			["push", address, "noteOff"].postln;
			this.valueNote(note, 0);
		}, noteNb);

		borderArray = [36, 39, 40, 43, 60, 63, 64, 67, 68, 71, 72, 75, 92, 95, 96, 99];
		if(borderArray.includes(noteNb), {this.setColor(0, 2, 1); offColor = [0, 2, 1]},
			{this.setColor(1, 4, 1); offColor = [1, 4, 1]});
	}


	valueNote { |note, vel|

		if(vel == 0, {
			this.setColor(offColor[0], offColor[1], offColor[2]);
		}, { // else
			this.setColor(1, 10, 1);
		});
		netaddr !? { netaddr.sendBundle(nil, [address, vel].postln) };

	}

	setColor {|r = 1, v = 4, b = 1|
		var preArray = [240, 71, 127, 21, 4, 0, 8, noteNb - 36, 0];
		var colorArray = [r, 0, v, 0, b, 0, 247];
		var intArray = Int8Array.newFrom(preArray ++ colorArray);
		midiout.sysex(intArray)
	}

}