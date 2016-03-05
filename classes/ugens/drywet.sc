FaustDrywet : UGen
{
  *ar { | in1, in2, mix(0.5) |
      ^this.multiNew('audio', in1, in2, mix)
  }

  *kr { | in1, in2, mix(0.5) |
      ^this.multiNew('control', in1, in2, mix)
  }

  checkInputs {
		^this.checkSameRateAsFirstInput
  }

  name { ^"FaustDrywet" }
}

