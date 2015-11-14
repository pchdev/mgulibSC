MGU_genDef {

	// arg defined attr.
	var parent, rate, wrapper;

	// returns
	var <varDecl, <varDef;
	var <oscil;

	*new { |parent, rate = \audio, wrapper|
		^this.newCopyArgs(parent, rate, wrapper).init;
	}

	init {
		varDef = "";
		this.buildVarDecl;
		this.buildOscil;
		this.buildVarDef;
	}

	refresh {
		this.init;
		wrapper.refresh
	}

	// SHORTCUT FNCTIONS

	buildEnvVar{ |env|
		^(env.defName + "=" + env.defNameKr ++ "(Env.newClear(8).asArray);\n")
	}

	envSeq { |envVar|
		^("EnvGen.kr(" ++ envVar ++ ")");
	}

	lfoSeq { |lfoBus|
		^("In.kr(" ++ lfoBus ++ ")");
	}

	modSeq { |param, mod|
		^("+ (" ++ param + "*" + mod ++ ")");
	}

	// VAR DECLARATIONS

	buildVarDecl {

		varDecl = "var";
		varDecl = varDecl + (parent.name ++ "_oscil");
		varDecl = varDecl ++ "," + (parent.ampEnv.defName);

		if(parent.hasENV_onFreq, { varDecl = varDecl ++ "," + parent.freqEnv.defName });
		if(parent.hasENV_onPhase, { varDecl = varDecl ++ "," + parent.phaseEnv.defName });
		if(parent.hasENV_onBeats, { varDecl = varDecl ++ "," + parent.beatsEnv.defName });
		if(parent.hasENV_onMorph, { varDecl = varDecl ++ "," + parent.morphEnv.defName });

		varDecl = varDecl ++ ";\n";
	}

	// BUILD OSCILLATOR PROCESS

	buildOscil {

		oscil = parent.name ++ "_oscil =";

		case

		{ parent.hasMorph } // Morph oscillator
		{ oscil = oscil + "VOsc.ar(" ++ parent.morph.defNameKr;
			if(parent.hasENV_onMorph, { oscil = oscil +
				this.modSeq(parent.morph.defNameKr, this.envSeq(parent.morphEnv.defName))});
			if(parent.hasLFO_onMorph, { oscil = oscil +
				this.modSeq(parent.morph.defNameKr, this.lfoSeq(parent.lfoMorphBus.defNameKr))})}

		{ (parent.hasChorus) && (parent.hasMorph == false) } // Chorusing oscillator
		{ oscil = oscil + "COsc.ar(" ++ parent.wavetable_bufnum.defNameKr }

		{ (parent.hasMorph == false) && (parent.hasChorus == false) } // Standard oscillator
		{ oscil = oscil + "Osc.ar(" ++ parent.wavetable_bufnum.defNameKr }; // end case

		oscil = oscil ++ ",";

		// freq seq
		oscil = oscil + parent.freq.defNameKr;
		if(parent.hasLFO_onFreq, { oscil = oscil +
			this.modSeq(parent.freq.defNameKr, this.lfoSeq(parent.lfoFreqBus.defNameKr))});
		if(parent.hasENV_onFreq, { oscil = oscil +
			this.modSeq(parent.freq.defNameKr, this.envSeq(parent.freqEnv.defName))});

		oscil = oscil ++ ",";

		// phase/beats seq
		if(parent.hasChorus, {
			oscil = oscil + parent.beats.defNameKr;
			if(parent.hasLFO_onBeats, { oscil = oscil +
				this.modSeq(parent.beats.defNameKr, this.lfoSeq(parent.lfoBeatsBus.defNameKr))});
			if(parent.hasENV_onBeats, { oscil = oscil +
				this.modSeq(parent.beats.defNameKr, this.envSeq(parent.beatsEnv.defName))})}, {
				oscil = oscil + parent.phase.defNameKr;
				if(parent.hasLFO_onPhase, { oscil = oscil +
					this.modSeq(parent.phase.defNameKr,
						this.lfoSeq(parent.lfoPhaseBus.defNameKr))});
				if(parent.hasENV_onPhase, { oscil = oscil +
					this.modSeq(parent.phase.defNameKr, this.envSeq(parent.phaseEnv.defName))})});

		oscil = oscil ++ ",";

		// mul seq
		oscil = oscil + parent.amp.defNameKr;
		oscil = oscil + "* EnvGen.kr(" ++ parent.ampEnv.defName ++ ","
		+ parent.gate.defNameKr ++ ", doneAction:" + parent.doneAction.defNameKr ++ ")";

		// oscil end
		oscil = oscil ++ ");\n";
	}

	// BUILD VAR DEFINITIONS

	buildVarDef {

		varDef = varDef ++ this.buildEnvVar(parent.ampEnv); // adding default ampEnv;

		if(parent.hasENV_onFreq, { // freqEnv
			varDef = varDef ++ this.buildEnvVar(parent.freqEnv)});
		if(parent.hasENV_onPhase, { // phaseEnv
			varDef = varDef ++ this.buildEnvVar(parent.phaseEnv)});
		if(parent.hasENV_onBeats, { // beatsEnv
			varDef = varDef ++ this.buildEnvVar(parent.beatsEnv)});
		if(parent.hasENV_onMorph, { // morphEnv
			varDef = varDef ++ this.buildEnvVar(parent.morphEnv)});

		varDef = varDef ++ oscil; // adding oscillator
	}

}	