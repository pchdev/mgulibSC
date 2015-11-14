FaustRfshClock : UGen
{
  *ar { | freq(1.0) |
      ^this.multiNew('audio', freq)
  }

  *kr { | freq(1.0) |
      ^this.multiNew('control', freq)
  } 

  name { ^"FaustRfshClock" }
}

