// without mul and add.
MGU_smoother : UGen {
    *ar { arg param = 440.0, length = 100;
        ^this.multiNew('audio', param, length)
    }
    *kr { arg param = 440.0, length = 100;
        ^this.multiNew('control', param, length)
    }
}

