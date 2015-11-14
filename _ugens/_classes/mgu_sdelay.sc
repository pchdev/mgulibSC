FaustMguSdelay : UGen
{
  *ar { | in1, dtime(0.2), fbk(50.0) |
      ^this.multiNew('audio', in1, dtime, fbk)
  }

  *kr { | in1, dtime(0.2), fbk(50.0) |
      ^this.multiNew('control', in1, dtime, fbk)
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

  name { ^"FaustMguSdelay" }
}

