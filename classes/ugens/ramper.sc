// without mul and add.
MGU_ramper : UGen {
    *ar { arg freq = 440.0, phase = 0.0, mul = 1.0, add = 0.0;
		^this.multiNew('audio', freq, phase).madd(mul, add)
    }
    *kr { arg freq = 440.0, phase, mul = 1.0, add = 0.0;
		^this.multiNew('control', freq, phase).madd(mul, add)
    }
}