MGU_simpleSine : MGU_AbstractModule {

	var freq;

	*new { |out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters
	}

	initParameters {

		type = \generator;

		freq = MGU_parameter(container, \freq, Float, [20, 20000], 440, true);

		def = SynthDef(name, {
			var process = SinOsc.ar(freq.kr);
			Out.ar(master_internal, process);
		}).add;

		this.initMasterOut();
	}
}