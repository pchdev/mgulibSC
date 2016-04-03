PO_inProcess : MGU_AbstractModule { // mic + comp + eq

	var <comp, <comp_threshold, <comp_ratio, <comp_makeup;
	var <lowcut, <lowcut_freq;
	var <eq1, <eq1_freq, <eq1_q, <eq1_db;
	var <eq2, <eq2_freq, <eq2_q, <eq2_db;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}

	initModule {

		// containers
		comp = MGU_container("compressor", container, node_group, 3127);
		lowcut = MGU_container("lowcut", container, node_group, 3127);
		eq1 = MGU_container("eq1", container, node_group, 3127);
		eq2 = MGU_container("eq2", container, node_group, 3127);

		// compressor
		comp_threshold = MGU_parameter(comp, \threshold, Float, [-100.0, 0.0], -40, true);
		comp_ratio = MGU_parameter(comp, \ratio, Integer, [0, 10], 3, true);
		comp_makeup = MGU_parameter(comp, \makeup, Float, [0, 24], 4.5, true);

		// lowcut
		lowcut_freq = MGU_parameter(lowcut, \freq, Integer, [20, 200], 121, true);

		// eq1
		eq1_freq = MGU_parameter(eq1, \freq, Integer, [100, 8000], 273, true);
		eq1_q = MGU_parameter(eq1, \q, Float, [0.0, 10.0], 2.0.reciprocal, true);
		eq1_db = MGU_parameter(eq1, \db, Integer, [-100, 24], -6, true);

		// eq2
		eq2_freq = MGU_parameter(eq2, \freq, Integer, [100, 8000], 1000, true);
		eq2_q = MGU_parameter(eq2, \q, Float, [0.0, 1.0], 1.0, true);
		eq2_db = MGU_parameter(eq2, \db, Integer, [-100, 24], 0, true);

		def = SynthDef(name, {
			var in, eq1, eq2, lowcut, comp, process;
			in = In.ar(inbus, num_inputs);
			lowcut = HPF.ar(in, lowcut_freq.kr);
			eq1 = BPeakEQ.ar(lowcut, eq1_freq.kr);
			eq2 = BPeakEQ.ar(eq1, eq2_freq.kr);
			comp = FaustComp.ar(eq2, 0.01, comp_ratio.kr, 0.01, comp_threshold.kr);
			process = comp * comp_makeup.kr;
			Out.ar(master_internal, process)
		}).add;


	}

}