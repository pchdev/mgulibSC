MGU_AbstractWavetableModule : MGU_AbstractModule {

	var numFrames, numPartials;
	var buffer;
	var <wavetable;
	var <waveform;

	*new { |out, server, name|
		^super.newCopyArgs(out, server, name);
	}

	initWavetable {

		numFrames = 16384;
		numPartials = 64;
		waveform = MGU_parameter(container, \waveform, Symbol,
			[\sine, \triangle, \square, \saw, \sawtooth], \sine);
		waveform.parentAccess = this;
		wavetable = Buffer.alloc(server, numFrames, 1);

	}

	paramCallBack { |param, value|
		switch(value[0],
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

	// note for anti-aliasing:
	// create a multi-buffer with MAXPARTIALS buffers
	// then sinesum each one of them with numPartials -1 and scale them not to go further than
	// Nyquist

	// for example with sawtooth: at 440Hz

	// [ 440, 880, 1320, 1760, 2200, 2640, 3080, 3520, 3960, 4400, 4840, 5280, 5720, 6160, 6600, 7040, 7480, 7920, 8360, 8800, 9240, 9680, 10120, 10560, 11000, 11440, 11880, 12320, 12760, 13200, 13640, 14080, 14520, 14960, 15400, 15840, 16280, 16720, 17160, 17600, 18040, 18480, 18920, 19360, 19800, 20240, 20680, 21120, 21560, 22000, 22440, 22880, 23320, 23760, 24200, 24640, 25080, 25520, 25960, 26400, 26840, 27280, 27720, 28160 ]
	// the wave should limit to 64-15 = 49 harmonics in order to go further than Nyquist
	// if oscillator's freq increases, buffer index should go lower etc.
}

