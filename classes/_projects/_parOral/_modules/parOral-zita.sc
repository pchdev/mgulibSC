PO_zita : MGU_AbstractModule { // faust zita_rev1

	var <indel, <lfx, <low_rt60, <mid_rt60, <hf_damping;
	var <eq1_freq, <eq1_lvl;
	var <eq2_freq, <eq2_lvl;
	var <mix, <lvl;

	*new { |out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters
	}

	initParameters {

		indel = MGU_parameter(container, \indel, Integer, [20, 100], 60, true);
		lfx = MGU_parameter(container, \lfx, Integer, [50, 1000], 200, true);
		low_rt60 = MGU_parameter(container, \low_rt60, Float, [1.0, 8.0], 3.0, true);
		mid_rt60 = MGU_parameter(container, \mid_rt60, Float, [1.0, 8.0], 2.0, true);
		hf_damping = MGU_parameter(container, \hf_damping, Integer, [1500, 23520], 5999, true);
		eq1_freq = MGU_parameter(container, \eq1_freq, Integer, [40, 2500], 315, true);
		eq1_lvl = MGU_parameter(container, \eq1_lvl, Float, [-15, 15], 0.0, true);
		eq2_freq = MGU_parameter(container, \eq2_freq, Integer, [40, 2500], 315, true);
		eq2_lvl = MGU_parameter(container, \eq2_lvl, Float, [-15, 15], 0.0, true);
		mix = MGU_parameter(container, \mix, Float, [-1.0, 1.0], 0.0, true);
		lvl = MGU_parameter(container, \level, Float, [-70, 40], -20.0, true);

		def = SynthDef(name, {
			var inleft, inright, verb, process;
			inleft = In.ar(inbus.smbKr);
			inright = In.ar(inbus.smbKr + 1);
			verb = FaustZitaRev1.ar(inleft, inright, indel.smbKr, lfx.smbKr,
				low_rt60.smbKr, mid_rt60.smbKr, hf_damping.smbKr, eq1_freq.smbKr,
				eq1_lvl.smbKr,
				eq2_freq.smbKr, eq2_lvl.smbKr, mix.smbKr, lvl.smbKr);
			Out.ar(out, verb);
		}).add;

	}

}