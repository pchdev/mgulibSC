Noiser : UGen {

	*ar { |mul = 1.0, add = 0.0|
		^this.multiNew(\audio).madd(mul, add);
	}

}