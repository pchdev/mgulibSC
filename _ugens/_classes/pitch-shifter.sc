FaustPitchShifter : UGen
{
  *ar { | in1, shift(0.0), window(1000.0), xfade(10.0) |
      ^this.multiNew('audio', in1, shift, window, xfade)
  }

  *kr { | in1, shift(0.0), window(1000.0), xfade(10.0) |
      ^this.multiNew('control', in1, shift, window, xfade)
  } 

  checkInputs {
    if (rate == 'audio', {
      1.do({|i|
        if (inputs.at(i).rate != 'audio', {
          ^(" input at index " + i + "(" + inputs.at(i) + 
            ") is not audio rate");
        });
      });
    });
    ^this.checkValidInputs
  }

  name { ^"FaustPitchShifter" }
}

