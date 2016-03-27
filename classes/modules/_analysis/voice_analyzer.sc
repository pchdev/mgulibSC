PO_voiceAnalyzer : MGU_AbstractModule {

	var <fftsize;
	var <thresh, <odftype, <relaxtime, <floor, <mingap, <medianspan;
	var <pitch, <onsets, <loudness;


	*new { |out = 0, server, num_inputs = 1, num_outputs = 1, name|
		^this.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\control)
		.init.initModule;
	}

	initModule {

		out.free;
		this.out = Bus.control(server, 1); // private control bus, no outputs

		// onset detection parameters
		fftsize = MGU_parameter(container, \fftsize, Integer, [0, inf], 2048, true);
		thresh = MGU_parameter(container, \thresh, Float, [-96, 12], -40, true, \dB, \amp);
		odftype = MGU_parameter(container, \odftype, Symbol, nil, \rcomplex, true);
		relaxtime = MGU_parameter(container, \relaxtime, Float, [0.0, inf], 1.0, true);
		floor = MGU_parameter(container, \floor, Float, [-96, 12], -40, true, \dB, \amp);
		mingap = MGU_parameter(container, \ingap, Integer, [0, inf], 10, true);
		medianspan = MGU_parameter(container, \medianspan, Integer, [0, inf], 11, true);
		fftsize = MGU_parameter(container, \fftsize, Integer, [512, inf], 512, true);

		// reply parameters

		pitch = MGU_parameter(container, \pitch, Float, [300, 3400], 440, true);
		onsets = MGU_parameter(container, \onsets, Integer, [0, 1], 0, true);
		loudness = MGU_parameter(container, \loudness, Float, [0, 64], 0, true);

		// osc functions

		OSCFunc({|msg|
			("reported pitch value:" + msg[1]).postln;
			pitch.val = msg[1]}, '/pitch');
		OSCFunc({|msg|
			("reported onset value:" + msg[1]).postln;
			pitch.val = msg[1]}, '/onset');
		OSCFunc({|msg|
			("reported loudness value:" + msg[1]).postln;
			pitch.val = msg[1]}, '/loudness');

		def = SynthDef(name, {
			var rate, in, chain, onset, loudness, pitch, trig;
			rate = 4;
			trig = Impulse.kr(4);
			in = In.ar(inbus, 1);
			chain = FFT(LocalBuf(fftsize.kr), in, 0.5, 1, 1);
			onset = Onsets.kr(chain, thresh.kr, odftype.kr, relaxtime.kr,
				floor.kr, mingap.kr, medianspan.kr, 1, 0);
			loudness = Loudness.kr(chain, 0.25, 1);
			pitch = Pitch.kr(in, 440, 300, 3400, rate, 16, 1,
				0.01, 0.5, 2, 0);
			SendReply.kr(trig, '/loudness', loudness);
			SendReply.kr(trig, '/pitch', pitch);
			SendReply.kr(onset, '/onset', onset, -1);
			Out.kr(out.index, onset);
		}).add;



	}



}