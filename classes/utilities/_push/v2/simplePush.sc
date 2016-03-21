MGU_simplePushInterface {

	var default, cc_array;
	var cc_targetparameter_array;
	var target_midi_device;

	*new { |default|
		^this.newCopyArgs(default).init
	}

	init {

		MIDIClient.init;

		target_midi_device = MIDIClient.sources;

		target_midi_device.size.do({|i|
			if((target_midi_device[i].device == "Ableton Push")
				&& (target_midi_device[i].name == "User Port"), {
				target_midi_device = i })
		});

		target_midi_device = MIDIOut(target_midi_device);

		MIDIIn.connectAll;

		cc_array = Array.fill(8, {|i|
			MIDIFunc.cc({|value, ccnum| this.parseCC(ccnum, value)}, 71+i)
		});

	}

	parseCC {|ccnum, value|
	}

	// LCD DISPLAY

	lcdDisplay { |string, line, divide = 1, slot = 1, align = \center|

		// divide should be : 8, 4, 2.. or 1
		// slot index starts at 1

		var res, intarray;
		line = line + 23;
		string = string.ascii;

		switch(align,
			\left, {
				if(string.size < 68) { string = string ++ Array.fill(68 - string.size, {|i| 32})}},
			\center, {
				var blankNb = ((68 - string.size) / 2);
				var impair = (blankNb % 2).asBoolean;
				if(impair, {
					var spacesBefore = Array.fill(blankNb - 1, {|i| 32});
					var spacesAfter = Array.fill(blankNb + 2, {|i| 32});
					var resString = spacesBefore ++ string ++ spacesAfter;
					string = resString }, { // else
					var spaceArray = Array.fill(blankNb, {|i| 32});
					var resString = spaceArray ++ string ++ spaceArray;
					string = resString;
			})
		});

		res = [240, 71, 127, 21, line, 0, 69, 0];
		res = res ++ string ++ [247];
		intarray = Int8Array.newFrom(res);
		target_midi_device.sysex(intarray);

	}

	lcdClearLine { |line|
		this.lcd_display("", line);
	}

	lcdClearSlot { |divide = 1, slot = 1|
		// tbi
	}

	lcdClearAll {
		4.do({|i| this.lcd_display("", i+1) });
	}


}