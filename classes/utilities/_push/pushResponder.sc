MGU_pushResponder {

	var <>target_midi_device_number;
	var target_midi_device;
	var uptoggles, downtoggles;

	*new { |target_midi_device|
		^this.newCopyArgs(target_midi_device).init
	}

	init {
		target_midi_device = MIDIOut(target_midi_device_number);
		uptoggles = Array.fill(8, {|i| i+20});
	}

	// BUTTON CONTROL functions

	setPadColor {|target, r = 1, v = 4, b = 1|
		var preArray = [240, 71, 127, 21, 4, 0, 8, target - 36, 0];
		var colorArray = [r, 0, v, 0, b, 0, 247];
		var intArray = Int8Array.newFrom(preArray ++ colorArray);
		target_midi_device.sysex(intArray);
	}

	setControl { |target, value|
		target_midi_device.control(0, target, value);

		/*
		0 - Off
		1 - Dim
		2 - Dim Blink
		3 - Dim Blink Fast
		4 - Lit
		5 - Lit Blink
		6 - Lit Blink Fast
		7 -> 127 - Lit
		*/

	}

	setControl_uptoggles { |value|
		switch(value,
			\off, {
				uptoggles.size.do({|i|
					this.setControl(uptoggles[i], 0)
			})}
		)
	}

	// LCD functions

	lcd_display { |string, line, divide = 1, slot = 1, align = \center|

		// divide should be : 8, 4, 2.. or 1 (you just didn't mean to divide by zero, didn't you..)
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

	lcd_clearLine { |line|
		this.lcd_display("", line);
	}

	lcd_clearSlot { |divide = 1, slot = 1|
		// tbi
	}

	lcd_clearAll {
		4.do({|i| this.lcd_display("", i+1) });
	}

}