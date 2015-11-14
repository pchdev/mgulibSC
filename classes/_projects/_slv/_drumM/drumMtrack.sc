MGU_drumMtrack {

	var numBars, timediv_default, currentBar;
	var barArray_hits, barArray_vel, barArray_timediv;

	*new { |numBars, timediv_default|
		^this.newCopyArgs(numBars,timediv_default).init
	}

	init {

		numBars ?? { numBars = 1 };
		timediv_default ?? { timediv_default = 16 };

		barArray_hits = Array(numBars);
		barArray_vel = Array(numBars);
		barArray_timediv = Array(numBars);

		numBars.do({|i|
			barArray_hits[i] = Array(timediv_default);
			barArray_vel[i] = Array(timediv_default);
			barArray_timediv[i] = timediv_default;
		});

	}


	readPattern { |path|

	}

	writePattern { |path|

	}

	addBar { |num|
		numBars = numBars + num;
	}

	removeBars { |indexArray|

	}

	prepareBar {

	}

	setBar {

	}

	exchangeBar {

	}

	addAlt {

	}

	prepareAlt {

	}

	setAlt {

	}

	hits { |hitsArray|

	}

	vel { |velArray|

	}

	fetch { |barIndex, beatIndex|

	}

	gotoBar { |index|

	}

	setTimeDiv {

	}

}



	