// without mul and add.
MGU_clock : UGen {
    *ar { arg freq = 440.0;
        ^this.multiNew('audio', freq)
    }
    *kr { arg freq = 440.0;
        ^this.multiNew('control', freq)
    }
}

