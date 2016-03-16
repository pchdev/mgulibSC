LfNoiser : UGen {

	*ar { |freq = 1.0, mul = 1.0, add = 0.0|
		^this.multiNew(\audio, freq).madd(mul, add)
	}

}
