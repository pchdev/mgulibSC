MGU_filterBuilder {

	var <name, <type;
	var <>hasLFO_onFreq, <>hasLFO_onQ;
	var <>hasENV_onFreq, <>hasENV_onQ;
	var <def;
	var args, varDecl, varDef, out;

	*new { |name, type,
		hasLFO_onFreq = false, hasLFO_onQ = false,
		hasENV_onFreq = false, hasENV_onQ = false|
		^this.newCopyArgs(name, type,
			hasLFO_onFreq, hasLFO_onQ,
			hasENV_onFreq, hasENV_onQ).init
	}

	init {
		def = "SynthDef(\\" ++ name ++ ", {|";
		args = "";
		varDecl = "var source;\n";
		varDef = "";
		out = "Out.ar(";
	}

	buildDef {

		var inBus, outBus;
		var freq, q, amp, bal;
		var lfoFreqBus, lfoQBus;
		var freqEnv, qEnv;
		var freqLFO_seq, freqENV_seq, freqLFOENV_seq;
		var qLFO_seq, qENV_seq, qLFOENV_seq;

		// single controls
		freq = name ++ "_freq";
		q = name ++ "_q";
		amp = name ++ "_amp";
		bal = name ++ "_bal";
		inBus = name ++ "_inBus";
		outBus = name ++ "_outBus";
		lfoFreqBus = name ++ "_lfoFreqBus";
		lfoQBus = name ++ "_lfoQBus";

		// lfo & env seq.
		freqLFO_seq = "+ (" + freq + "*" + "In.kr(" ++ lfoFreqBus ++ "))";
		freqENV_seq = "* EnvGen.kr(" ++ freqEnv ++ ")";

		qLFO_seq = "+ (" + freq + "*" + "In.kr(" ++ lfoQBus ++ "))";
		qENV_seq = "* EnvGen.kr(" ++ qEnv ++ ")";

		// arg. seq.
		args = args ++ inBus ++ "," + outBus ++ "," + freq ++ "," + q ++ "," + amp ++ "," + bal;
		if(hasLFO_onFreq, {args = args ++ "," + lfoFreqBus});
		if(hasLFO_onQ, {args = args ++ "," + lfoQBus});
		args = args ++ "|\n";

		// var decl. & def.
		if(hasENV_onFreq, { varDecl = varDecl ++ "var" + freqEnv ++ ";\n";
			varDef = varDef ++ freqEnv + "= \\" ++ freqEnv ++ ".kr(Env.newClear(8).asArray;\n"});
		if(hasENV_onQ, { varDecl = varDecl ++ "var" + qEnv ++ ";\n";
			varDef = varDef ++ qEnv + "= \\" ++ qEnv ++ ".kr(Env.newClear(8).asArray;\n"});

		// source def.
		varDef = varDef ++ "source =";
		switch(type,
			\allpass, { varDef = varDef + "BAllPass" },
			\bandpass, { varDef = varDef + "BBandPass" },
			\bandstop, { varDef = varDef + "BBandStop" },
			\hipass, { varDef = varDef + "BHiPass" },
			\hipass4, { varDef = varDef + "BHiPass4" },
			\lopass, { varDef = varDef + "BLowPass" },
			\lopass4, { varDef = varDef + "BLowPass4" });

		// inBus
		varDef = varDef ++ ".ar(In.ar(" ++ inBus ++ ", 2),";

		// freq
		varDef = varDef + freq;
		if(hasLFO_onFreq, { varDef = varDef + freqLFO_seq });
		if(hasENV_onFreq, { varDef = varDef + freqENV_seq });

		// q
		varDef = varDef ++ ",";
		varDef = varDef + q;
		if(hasLFO_onQ, { varDef = varDef + qLFO_seq });
		if(hasENV_onQ, { varDef = varDef + qENV_seq });

		// mul
		varDef = varDef ++ ",";
		varDef = varDef + amp;

		varDef = varDef ++ ");\n";

		out = out ++ outBus ++ ",";
		out = out + "Mix.ar([In.ar(" ++ inBus ++ ", 2) * (1 -" + bal ++ "), (source *" + bal ++ ")]))";
		out = out ++ ";\n";

		def = def ++ args ++ varDecl ++ varDef ++ out ++ "}).add;";


	}
}

	