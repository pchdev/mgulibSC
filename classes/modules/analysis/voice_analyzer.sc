PO_voiceAnalyzer : MGU_AbstractModule {

	var <fftsize;
	var <thresh, <odftype, <relaxtime, <floor, <mingap, <medianspan;
	var <pitch, <onsets, <loudness;
	var <netaddr, <fastmul, <mindur;


	*new { |out = 0, server, num_inputs = 1, num_outputs = 0, name|
		^this.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\analyzer)
		.init.initModule;
	}

	initModule {

		description = "onset detector + pitch & loudness tracker";

		netaddr = NetAddr("192.168.0.2", 9995);

		// onset detection parameters

		fftsize = MGU_parameter(container, \fftsize, Integer, [0, inf], 2048, true);
		thresh = MGU_parameter(container, \thresh, Float, [-96, 12], -40, true, \dB, \amp);
		odftype = MGU_parameter(container, \odftype, Symbol, nil, \mk1, true);
		relaxtime = MGU_parameter(container, \relaxtime, Float, [0.0, inf], 1.0, true);
		floor = MGU_parameter(container, \floor, Float, [-96, 12], -40, true, \dB, \amp);
		mingap = MGU_parameter(container, \ingap, Integer, [0, inf], 10, true);
		medianspan = MGU_parameter(container, \medianspan, Integer, [0, inf], 11, true);

		fastmul = MGU_parameter(container, \fastmul, Float, [0, 1], 0.5, true);
		mindur = MGU_parameter(container, \mindur, Integer, [100, 2000], 100, true, \ms, \s);

		// reply parameters

		pitch = MGU_parameter(container, \pitch, Float, [0, 1000], 440, true);
		loudness = MGU_parameter(container, \loudness, Float, [0, 1000], 0, true);
		onsets = MGU_parameter(container, \onsets, Integer, [0, 1], 0, true);

		// osc functions

		OSCFunc({|msg|
			var ptch = msg[3] - 100;
			//("reported pitch value:" + ptch).postln;
			pitch.val = ptch}, '/pitch');
		OSCFunc({|msg|
			//("reported onset value:" + msg[3]).postln;
			onsets.val = msg[3]}, '/onset');
		OSCFunc({|msg|
			var l = msg[3];
			//("reported loudness value:" + l).postln;
			loudness.val = l}, '/loudness');

		// def

		def = SynthDef(name, {
			var rate, in, filtered_in, chain, onset, loudness, pitch, trig, peak, rms;
			rate = 16;
			trig = Impulse.kr(rate);
			in = In.ar(inbus, 1);
			filtered_in = LPF.ar(in, 1000);
			chain = FFT(LocalBuf(fftsize.kr), in, 0.5, 1, 1);
			//onset = Onsets.kr(chain, thresh.kr, odftype.kr, relaxtime.kr,
				//floor.kr, mingap.kr, medianspan.kr, 1, 0);
			onset = Coyote.kr(in, 0.2, 0.2, 0.01, fastmul.kr, thresh.kr, mindur.kr);
			loudness = Loudness.kr(chain, 0.25, 1);
			pitch = Pitch.kr(filtered_in, 440, 100, 1000, rate, 16, 1,
				0.01, 0.5, 2, 0);
			peak = PeakFollower.kr(in, 0.999);
			peak = BinaryOpUGen('>', peak, thresh.kr);
			rms = RunningSum.kr(loudness, 4410) * 4410.reciprocal;
			SendReply.kr(trig, '/loudness', rms);
			SendReply.kr(trig, '/pitch', pitch);
			SendReply.kr(trig, '/onset', peak, -1); // no output needed
		}).add;

	}
/*
	paramCallBack {|param, value|

		case

		{param == \pitch} { netaddr.sendBundle(nil,['/trigger/pitch', value]) }
		{param == \loudness} { netaddr.sendBundle(nil, ['/trigger/loudness', value]) }
		{param == \onset} { netaddr.sendBundle(nil, ['/trigger/onsets', value]) };


	}

	*/





}