MGU_simpleSine : MGU_AbstractModule {

	var <freq;

	*new { |out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).type_(\generator)
		.init.initModule.initMasterOut;
	}

	initModule {

		// init parameters first
		freq = MGU_parameter(container, \freq, Float, [20, 20000], 440, true);

		// then init synthdef, with .add method
		def = SynthDef(name, {
			var process = SinOsc.ar(freq.kr);
			Out.ar(master_internal, process);
		}).add;

	}
}