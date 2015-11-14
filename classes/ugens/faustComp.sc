FaustComp : UGen
{
  *ar { | in1, attack(0.01), ratio(2.0), release(0.01), thresh(-30.0) |
      ^this.multiNew('audio', in1, attack, ratio, release, thresh)
  }

  *kr { | in1, attack(0.01), ratio(2.0), release(0.01), thresh(-30.0) |
      ^this.multiNew('control', in1, attack, ratio, release, thresh)
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

  name { ^"FaustComp" }
}

