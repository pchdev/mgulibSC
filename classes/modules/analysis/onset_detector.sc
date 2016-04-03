MGU_onsetDetector : MGU_AbstractModule {

	var <fftsize;
	var <thresh, <odftype, <relaxtime, <floor, <mingap, <medianspan;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 1, name|
		^this.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\analyzer)
		.init.initModule;
	}

	initParameters {

		out.free;
		out = Bus.control(server, 1);

		fftsize = MGU_parameter(container, \fftsize, Integer, [0, inf], 2048, true);
		thresh = MGU_parameter(container, \thresh, Float, [-96, 12], -40, true, \dB, \amp);
		odftype = MGU_parameter(container, \odftype, Symbol, nil, \rcomplex, true);
		relaxtime = MGU_parameter(container, \relaxtime, Float, [0.0, inf], 1.0, true);
		floor = MGU_parameter(container, \floor, Float, [-96, 12], -40, true, \dB, \amp);
		mingap = MGU_parameter(container, \ingap, Integer, [0, inf], 10, true);
		medianspan = MGU_parameter(container, \medianspan, Integer, [0, inf], 11, true);
		fftsize = MGU_parameter(container, \fftsize, Integer, [512, inf], 512, true);

		def = SynthDef(name, {
			var chain, onset;
			chain = FFT(LocalBuf(fftsize.kr), In.ar(inbus.kr), 0.5, 1, 1);
			onset = Onsets.kr(chain, thresh.kr, odftype.kr, relaxtime.kr,
				floor.kr, mingap.kr, medianspan.kr, 1, 0);
			SendReply.kr(onset, '/onset', onset, -1);
			Out.kr(out.index, onset);
		}).add;

	}

}