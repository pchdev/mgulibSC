MGU_inModule : MGU_AbstractModule {

	*new { |out = 0, server, num_inputs = 1, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\generator)
		.init.initModule.initMasterDef
	}

	initModule {

		def = SynthDef(name, {
			var in = SoundIn.ar(0);
			Out.ar(master_internal, in);
		}).add

	}

}