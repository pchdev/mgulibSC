MGU_kEnvADSR : MGU_AbstractModule {

	var <attack, <decay, <sustain, <release;
	var <gate, <doneAction;

	*new { |out = 0, server, numInputs = 0, numOutputs = 1, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\control)
		.init.initModule
	}

	initModule {

		attack = MGU_parameter(container, \attack, Float, [5, 5000], 10, true, \ms, \s);
		decay = MGU_parameter(container, \decay, Float, [5, 2000], 300, true, \ms, \s);
		sustain = MGU_parameter(container, \sustain, Float, [0, 1], 1, true);
		release = MGU_parameter(container, \release, Float, [5, 10000], 1000, true, \ms, \s);
		gate = MGU_parameter(container, \gate, Integer, [0, 1], 1, true);
		doneAction = MGU_parameter(container, \doneAction, Integer, [0, 14], 13, true);

		def = SynthDef(name, {
			var process;
			process = EnvGen.kr(Env.adsr(attack.kr, decay.kr, sustain.kr, release.kr),
				gate.kr, doneAction: doneAction.kr);
			Out.kr(out, process)
		}).add;
	}

}