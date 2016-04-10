MGU_shaper : MGU_AbstractBufferModule {

	var <in_gain;

	*new {|out = 0, server, num_inputs = 1, num_outputs = 1, name|
		^super.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef;
	}

	initModule {

		in_gain = MGU_parameter(container, \in_gain, Float, [-96, 12], 0, true, \dB, \amp);
		buffer = Buffer.alloc(server, 1024, 1, {|buf|
			buf.chebyMsg([0.25, 0.5, 0.25, 0.5, 0.125, 0.25], false)});

		def = SynthDef(name, {
			var in = In.ar(inbus) * in_gain.kr;
			var process = Shaper.ar(buffer, in);
			Out.ar(master_internal, process)
		}).add;


	}

}