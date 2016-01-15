MGU_inModule : MGU_AbstractModule {

	*new { |out, server, numInputs = 1, numOutputs = 1, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\generator)
		.init.initModule.initMasterDef
	}

	initModule {

		def = SynthDef(name, {
			var in = SoundIn.ar(0);
			Out.ar(master_internal, in);
		}).add

	}

}