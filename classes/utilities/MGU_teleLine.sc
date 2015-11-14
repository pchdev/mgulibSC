MGU_teleLine {

	// instance variables

	var <>start, <>stop, <>duration, <>pause, <>nbFrames;
	var <>lineArray;

	// class methods

	*new { |start, stop, duration, pause, nbFrames = 512|
		^this.newCopyArgs(start, stop, duration, pause, nbFrames).init;
	}

	init {

		var scale, phaseIncr;
		scale = stop - start;
		phaseIncr = scale / nbFrames;

		lineArray = Array.newClear(nbFrames);

		for(0, nbFrames - 1, {|i|
			var incr = 0.0;
			incr = (i * phaseIncr) + start;
			lineArray.put(i, incr);
		});

	}

	trig { |function, freqMs = 20, doneAction|
		var routine, scale, nbSamples, phase;
		nbSamples = duration / freqMs;
		phase = nbFrames / nbSamples;

		routine = Routine.new({

			forBy(0, nbFrames - 1, phase,{ |i|
				function.value(lineArray.at(i.asInteger));
				(freqMs / 1000).yield;
			});

			doneAction.value;
		});

		SystemClock.play(routine);

		}

}