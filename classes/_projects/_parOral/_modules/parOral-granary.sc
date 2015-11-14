/*PO_granary : MGU_AbstractModule { // granular delay module

	var <>numFrames, <>numChannels;
	var <grainSize, <grainPitch,<grainPhase, <randCoeff, <randFreq, startPos;
	var buffer, numGrains;

	*new {|name, out, server, numFrames = 44100, numChannels = 1|
		^super.newCopyArgs(name, out, server, numFrames, numChannels).init.initParameters
	}

	initParameters {

		numGrains = 16;

		grainSize = MGU_parameter(container, \grainSize, Integer, [-inf, inf], 50, true,
			\ms, \samps)
		//grainPitch = MGU_parameter(container, \grainPitch, Float, [-12, 12],
			//Array.fill(numGrains, { 0.0 }), true);
		grainPhase = MGU_parameter(container, \grainPhase, Float, [0, 1],
			Array.fill(numGrains, {|i| numGrains.reciprocal * i}), true);
		randCoeff = MGU_parameter(container, \randCoeff, Integer, [-inf, inf], 20, true);
		randFreq = MGU_parameter(container, \randFreq, Float, [0, inf], 0.05, true);

		buffer = Buffer.alloc(server, numFrames, numChannels);


		def = SynthDef(name, {

			var randoffset, bufwr, mainphase;
			var graincount2 = -1;
			randoffset = LFNoise1.ar(randFreq.smbKr) * randCoeff.smbKr;
			bufwr = BufWr.ar(inbus.smbKr, buffer.bufnum, Phasor.ar(0,
				BufRateScale.kr(buffer.bufnum), 0, BufFrames.kr(buffer.bufnum)), 1);
			graincount = Impulse.ar((grainSize.smbKr / SampleRate.ir).reciprocal) * grainSize;
			graincount2 = graincount



		}).add;

	}

}*/