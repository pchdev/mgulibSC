MGU_simpleSine : MGU_AbstractModule {

	var <freq;

	*new { |out = 0, server, num_inputs = 1, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\generator)
		.init.initModule.initMasterDef;
	}

	initModule {

		// init parameters first
		freq = MGU_parameter(container, \freq, Float, [20, 20000], 440, true);

		// then init synthdef, with .add method
		def = SynthDef(name, {
			var process = SinOsc.ar(MGU_modParameter.kr(freq));
			Out.ar(master_internal, process);
		}).add;

	}
}