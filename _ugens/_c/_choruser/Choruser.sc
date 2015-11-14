Choruser : UGen {

	*ar { |in = 0.0, freq = 1.0, depth = 100.0, dtime = 50.0, fbk = 20.0, drywet = 50.0, mul = 1.0, add = 0.0|
		^this.multiNew(\audio, in, freq, depth, dtime, fbk, drywet).madd(mul, add)
	}

	/**kr { |in = 0.0, freq = 1.0, depth = 100.0, dtime = 50.0, fbk = 20.0, mul = 1.0, add = 0.0|
		^this.multiNew(\control, freq, depth, dtime, fbk).madd(mul, add)
	}*/

	// checkInputs { ^this.checkSameRateAsFirstInput }
}
