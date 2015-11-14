PO_chorusMTS : MGU_AbstractModule {

	// mono to stereo

	var <dtime_left, <dtime_right;
	var <fbk_left, <fbk_right;
	var <freq;
	var <depth, <drywet;

	*new { |out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters
	}

	initParameters {

		dtime_left = MGU_parameter(container, \dtime_left, Float, [0.001, 0.1], 0.2, true);
		dtime_right = MGU_parameter(container, \dtime_right, Float, [0.001, 0.1], 0.2, true);
		fbk_left = MGU_parameter(container, \fbk_left, Float, [0, 100], 50, true);
		fbk_right = MGU_parameter(container, \fbk_right, Float, [0, 100], 50, true);
		freq = MGU_parameter(container, \freq, Float, [0, 100], 0.2, true);
		depth = MGU_parameter(container, \depth, Float, [0, 100], 50, true);
		drywet = MGU_parameter(container, \drywet, Float, [0.0, 1.0], 0.5, true);

		def = SynthDef(name, {
			var chorusL, chorusR, process, in;
			in = In.ar(inbus.smbKr);
			chorusL = ChoruserSC.ar(in, freq.smbKr, depth.smbKr,
				dtime_left.smbKr, fbk_left.smbKr);
			chorusR = ChoruserSC.ar(in, freq.smbKr, depth.smbKr,
				dtime_right.smbKr, fbk_right.smbKr);
			process = Mix.ar([in * (1 - drywet.smbKr),
				chorusL * drywet.smbKr,
				chorusR * drywet.smbKr]);
			Out.ar(out, process);
		}).add;
	}

}

PO_chorusMTM : MGU_AbstractModule {

}

PO_chorusSTS : MGU_AbstractModule {

}