MGU_modParameter {
	*kr { |param|
		var process;
		process = param.kr + (param.kr * In.kr(param.kbus, 1));
		param.kbus.postln;
		^process
	}
}