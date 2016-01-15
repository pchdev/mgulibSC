PO_zitaSTS : MGU_AbstractModule { // faust zita_rev1

	var <indel, <lfx, <low_rt60, <mid_rt60, <hf_damping;
	var <eq1_freq, <eq1_lvl;
	var <eq2_freq, <eq2_lvl;
	var <mix, <lvl;

	*new { |out = 0, server, numInputs = 2, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		indel = MGU_parameter(container, \indel, Integer, [20, 100], 60, true);
		lfx = MGU_parameter(container, \lfx, Integer, [50, 1000], 200, true);
		low_rt60 = MGU_parameter(container, \low_rt60, Float, [1.0, 8.0], 3.0, true);
		mid_rt60 = MGU_parameter(container, \mid_rt60, Float, [1.0, 8.0], 2.0, true);
		hf_damping = MGU_parameter(container, \hf_damping, Integer, [1500, 23520], 5999, true);
		eq1_freq = MGU_parameter(container, \eq1_freq, Integer, [40, 2500], 315, true);
		eq1_lvl = MGU_parameter(container, \eq1_lvl, Float, [-15, 15], 0.0, true);
		eq2_freq = MGU_parameter(container, \eq2_freq, Integer, [40, 2500], 315, true);
		eq2_lvl = MGU_parameter(container, \eq2_lvl, Float, [-15, 15], 0.0, true);

		def = SynthDef(name, {
			var in, verb, process;
			in = In.ar(inbus, numInputs);
			verb = FaustZitaRev1.ar(in[0], in[1], indel.kr, lfx.kr,
				low_rt60.kr, mid_rt60.kr, hf_damping.kr, eq1_freq.kr,
				eq1_lvl.kr,
				eq2_freq.kr, eq2_lvl.kr, 0, 0);
			Out.ar(master_internal, verb);
		}).add;

	}

}