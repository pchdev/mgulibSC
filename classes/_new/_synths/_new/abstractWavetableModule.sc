MGU_AbstractWavetableModule : MGU_AbstractModule {

	var numFrames, numPartials;
	var buffer;
	var wavetable;
	var waveform;

	*new { |out, server, name|
		^super.newCopyArgs(out, server, name)
	}

	initWavetable {

		numFrames = 16384;
		numPartials = 64;
		waveform = MGU_parameter(container, \waveform, Symbol, nil, \sine);
		waveform.parentAccess = this;
		wavetable = Buffer.alloc(server, numFrames, 1);

	}

	paramCallBack { |param, value|
		switch(value,
			\sine, { wavetable.sine1(this.buildSine) },
			\triangle, { wavetable.sine1(this.buildTriangle) },
			\square, { wavetable.sine1(this.buildSquare) },
			\saw, { wavetable.sine1(this.buildSaw) },
			\sawtooth, { wavetable.sine1(this.buildSaw) });
	}

	buildCustom1 { |ampArray|
		wavetable.sine1(ampArray);
	}

	buildCustom2 { |freqArray, ampArray|
		wavetable.sine2(freqArray, ampArray);
	}

	buildCustom3 { |freqArray, ampArray, phaseArray|
		wavetable.sine3(freqArray, ampArray, phaseArray)
	}

	plot {
		wavetable.plot
	}

		// PRIVATE MTHODS

	buildSine {
		var sineArray;
		sineArray = Array.fill(numPartials, { 0 });
		sineArray = sineArray.put(0, 1);
		^sineArray;

	}

	buildTriangle {
		var triArray;
		triArray = Array.fill(numPartials, {|i|
			var j = i + 1;
			var partial;
			var other_partial = j % 4;
			if(j % 2 == 0,
				{ partial = 0 },
				{ partial = j.squared.reciprocal });
			if(other_partial == 3,
				{ partial = partial * -1 });
			partial });
		^triArray;
	}

	buildSquare {
		var squareArray;
		squareArray = Array.fill(numPartials, {|i|
			var j = i + 1;
			var partial;
			if (j % 2 == 0,
				{ partial = 0 },
				{ partial = j.reciprocal });
			partial });
		^squareArray
	}

	buildSaw {
		var sawArray;
		sawArray = Array.fill(numPartials, {|i|
			(i + 1).reciprocal});
		^sawArray;
	}
}