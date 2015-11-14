FaustLfnoise : UGen
{
  *ar { | freq(1.0), phase(0.0) |
      ^this.multiNew('audio', freq, phase)
  }

  *kr { | freq(1.0), phase(0.0) |
      ^this.multiNew('control', freq, phase)
  } 

  name { ^"FaustLfnoise" }
}

