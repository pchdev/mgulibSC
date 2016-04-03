MGU_pan2 : MGU_AbstractModule {

	var <pos;

	*new {|out = 0, server, num_inputs = 1, num_outputs = 2, name|
		^this.newCopyArgs(out, server, num_inputs, num_outputs, name).type_(\effect)
		.init.initModule.initMasterDef;
	}

	initModule {

		var pos = MGU_parameter(container, \pos, Float, [-1, 1], 0, true);
		mix.val = 1;

		def = SynthDef(name, {
			var in, process;
			in = In.ar(inbus, num_inputs);
			process = Pan2.ar(in, pos.kr);
			Out.ar(master_internal, process);
		}).add;

	}


}