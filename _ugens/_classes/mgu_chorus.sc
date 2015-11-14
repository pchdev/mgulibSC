FaustMguChorus : UGen
{
  *ar { | in1, delaytime(10.0), depth(1.0), feedback(50.0), freq(1.0) |
      ^this.multiNew('audio', in1, delaytime, depth, feedback, freq)
  }

  *kr { | in1, delaytime(10.0), depth(1.0), feedback(50.0), freq(1.0) |
      ^this.multiNew('control', in1, delaytime, depth, feedback, freq)
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

  name { ^"FaustMguChorus" }
}

