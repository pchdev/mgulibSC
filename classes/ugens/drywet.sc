FaustDrywet : MultiOutUGen
{
  *ar { | in1, in2, mix(0.5) |
      ^this.multiNew('audio', in1, in2, mix)
  }

  *kr { | in1, in2, mix(0.5) |
      ^this.multiNew('control', in1, in2, mix)
  } 

  checkInputs {
    if (rate == 'audio', {
      2.do({|i|
        if (inputs.at(i).rate != 'audio', {
          ^(" input at index " + i + "(" + inputs.at(i) + 
            ") is not audio rate");
        });
      });
    });
    ^this.checkValidInputs
  }

  init { | ... theInputs |
      inputs = theInputs
      ^this.initOutputs(2, rate)
  }

  name { ^"FaustDrywet" }
}

