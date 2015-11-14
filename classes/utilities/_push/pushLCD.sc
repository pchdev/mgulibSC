MGU_pushLCD {

	var midiout;

	*new {|midiout|
		^this.newCopyArgs(midiout).init
	}

	init {

		// lcd display
		// length = 68 char
		// line 1 = 24, line 2 = 25, line 3 = 26, line 4 = 27
		// ++ Array of 68 atoi char
		// ++ 247

	}

	display { |string, line, align = \center|

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
					string = resString }, {
					var spaceArray = Array.fill(blankNb, {|i| 32});
					var resString = spaceArray ++ string ++ spaceArray;
					string = resString;
			})
		});
		string.size.postln;
		res = [240, 71, 127, 21, line, 0, 69, 0];
		res = res ++ string ++ [247];
		intarray = Int8Array.newFrom(res);
		midiout.sysex(intarray);

	}

	clearLine { |line|
		this.display("", line);
	}

	clearAll {
		4.do({|i| this.display("", i+1) });
	}

}