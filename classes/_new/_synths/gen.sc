MGU_gen {

	classvar instanceCount;

	// arg defined attr
	var <name, server, parentContainer, wrapper;

	// accesses
	var <lfoFreqBus, <lfoPhaseBus, <lfoMorphBus;

	// private
	var <container;
	var wavetable, <genDef;
	var firstBuf, lastBuf;

	// public parameters
	var <waveform;
	var <wavetable_bufnum;
	var <defaultEnv, <ampEnv, <freqEnv, <phaseEnv, <beatsEnv, <morphEnv;
	var <freq, <amp, <phase, <beats, <morph;
	var <gate, <doneAction, <firstBuf, <lastBuf;

	// read only
	var <hasChorus, <hasMorph;
	var <hasLFO_onFreq, <hasLFO_onPhase, <hasLFO_onBeats, <hasLFO_onMorph;
	var <hasENV_onFreq, <hasENV_onPhase, <hasENV_onBeats, <hasENV_onMorph;

	// CONSTRUCT. SEQ.
	*new { |name, server, parentHub, wrapper|
		^this.newCopyArgs(name, server, parentHub, wrapper).init;
	}

	init {

		// default-global
		instanceCount !? { instanceCount = instanceCount + 1 };
		instanceCount ?? { instanceCount = 1 };

		server ?? { server = Server.default };

		// init subunits
		wavetable = MGU_wavetable(server);
		container = MGU_container(name, parentContainer: parentContainer);

		// init parameters

		defaultEnv = Env([0, 1, 0], [2, 2], \lin, 1);

		wavetable_bufnum = MGU_parameter(container, \wavetableID, Integer,
			[0, inf], default: this.bufnum);
		waveform = MGU_parameter(container, \waveform, Symbol,
			default: \sine);
		freq = MGU_parameter(container, \freq, Float,
			[0.0, 22000.0], default: 440.0);
		amp = MGU_parameter(container, \amp, Float,
			[0.0, 1.0], default: 1.0);
		phase = MGU_parameter(container, \phase, Float,
			[0.0, 1.0], default: 0.0);
		morph = MGU_parameter(container, \morph, Float,
			[0.0, inf], default: this.bufnum.asFloat);
		ampEnv = MGU_parameter(container, \ampEnv, Env,
			default: defaultEnv);
		gate = MGU_parameter(container, \gate, Integer,
			[0, 1], default: 1);
		doneAction = MGU_parameter(container, \doneAction,
			Integer, [0, 14], default: 2);
		firstBuf = MGU_parameter(container, \firstBuf,
			Integer, [0, inf], default: 0);
		lastBuf = MGU_parameter(container, \lastBuf,
			Integer, [1, inf], default: 1);

		// init booleans
		hasChorus = false; hasMorph = false;
		hasLFO_onFreq = false; hasLFO_onPhase = false;
		hasLFO_onBeats = false; hasLFO_onMorph = false;
		hasENV_onFreq = false; hasENV_onPhase = false;
		hasENV_onBeats = false; hasENV_onMorph = false;

		// init genDef
		genDef = MGU_genDef(this, \audio, wrapper);
	}

	// PUBLIC MTHODS

	buffer {
		^wavetable.mainBuf;
	}

	bufnum {
		^wavetable.mainBuf.bufnum
	}

	plot {
		wavetable.mainBuf.plot
	}

	waveform_ {|aSymbol|
		waveform.val = aSymbol;
		wavetable.replaceWith(aSymbol);
	}

	morphTo {|waveTarget, node, morphDuration = 3000| // no control /!\
		var teleLine = MGU_teleLine(morph.val, morph.val + 1, morphDuration);
		wavetable.addNext(waveTarget);
		teleLine.trig({|val| node.set(morph.defName, val); val.postln});
		morph.val = morph.val + 1;
		waveform.val = waveTarget;
	}

	addChorus {
		hasChorus = true;
		beats = MGU_parameter(container, \beats, Float,
			[0.0, 10.0], default: 0.5);
		genDef.refresh;
	}

	removeChorus {
		hasChorus = false;
		genDef.refresh;
	}

	addMorph {
		hasMorph = true;
		wavetable.addNext(waveform.val);
		morph.val_(morph.val + 0.999, false);
		genDef.refresh;
	}

	removeMorph {
		hasMorph = false;
		genDef.refresh;
	}

	// LFOs only should access these methods <-

	addLFO_onFreq { |busIndex|
		hasLFO_onFreq = true;
		lfoFreqBus = MGU_parameter(container, \lfoFreqBus, Integer,
			[0, inf], default: busIndex);
		genDef.refresh;
	}

	removeLFO_onFreq {
		hasLFO_onFreq = false;
		genDef.refresh;
	}

	addLFO_onPhase { |busIndex|
		hasLFO_onPhase = true;
		lfoPhaseBus = MGU_parameter(container, \lfoPhaseBus, Integer,
			[0, inf], default: busIndex);
		genDef.refresh;
	}

	removeLFO_onPhase {
		hasLFO_onPhase = false;
		genDef.refresh;
	}

	addLFO_onMorph { |busIndex|
		hasLFO_onMorph = true;
		lfoMorphBus = MGU_parameter(container,\lfoMorphBus, Integer,
			[0, inf] default: busIndex);
		genDef.refresh;
	}

	removeLFO_onMorph {
		hasLFO_onMorph = false;
		genDef.refresh;
	}

	// Envelopes <-

	addEnv_onFreq { |env|
		env ?! { freqEnv = MGU_parameter(container, \freqEnv, Env,
			default: env) };
		env ?? { freqEnv = MGU_parameter(container, \freqEnv, Env,
			default: defaultEnv) };
		hasENV_onFreq = true;
		genDef.refresh;
	}

	removeEnv_onFreq {
		freqEnv = nil;
		hasENV_onFreq = false;
		genDef.refresh;
	}

	addEnv_onPhase { |env|
		env ?! { phaseEnv = MGU_parameter(container, \phaseEnv, Env,
			default: env) };
		env ?? { phaseEnv = MGU_parameter(container, \phaseEnv, Env,
			default: defaultEnv) };
		hasENV_onPhase = true;
		genDef.refresh;
	}

	removeEnv_onPhase {
		phaseEnv = nil;
		hasENV_onPhase = false;
		genDef.refresh;
	}

	addEnv_onBeats { |env|
		env ?! { beatsEnv = MGU_parameter(container, \beatsEnv, Env,
			default: env) };
		env ?? { beatsEnv = MGU_parameter(container, \beatsEnv, Env,
			default: defaultEnv) };
		hasENV_onBeats = true;
		genDef.refresh;
	}

	removeEnv_onBeats {
		phaseEnv = nil;
		hasENV_onBeats = false;
		genDef.refresh;
	}

	addEnv_onMorph { |env|
		env ?! { morphEnv = MGU_parameter(container, \morphEnv, Env,
			default: env) };
		env ?? { morphEnv = MGU_parameter(container, \morphEnv, Env,
			default: defaultEnv) };
		hasENV_onMorph = true;
		genDef.refresh;
	}

	removeEnv_onMorph {
		morphEnv = nil;
		hasENV_onMorph = false;
		genDef.refresh;
	}

}	