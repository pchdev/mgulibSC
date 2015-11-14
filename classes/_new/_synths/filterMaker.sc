MGU_filterMaker {

	var <name, type, <server;
	var <>inBus, <>outBus;
	var <>freq, <>q, <>amp, <>bal;
	var <lfoFreqBus, <lfoQBus;
	var <>freqEnv, <>qEnv;

	// private
	var <builder;

	*new { |name, type, server, inBus, outBus = 0|
		^this.newCopyArgs(name, type, server, inBus, outBus).init
	}

	init {

		freq = 1200;
		q = 1;
		amp = 1;
		bal = 1;

		builder = MGU_filterBuilder(name, type);
		builder.buildDef;
		builder.def.interpret;

	}

	def {
		^builder.def
	}

	addLFO_onFreq { |busIndex|
		builder.hasLFO_onFreq = true;
		lfoFreqBus = busIndex;
		builder.init;
		builder.buildDef;
		builder.def.interpret;
	}

	makeFilter {|addAction = 1, target = 1|
		var synthID;
		synthID = server.nextNodeID;

		server.sendBundle(nil, ["/s_new", name, synthID, addAction, target,
			name ++ "_bal", bal,
			name ++ "_inBus", inBus,
			name ++ "_outBus", outBus,
			name ++ "_freq", freq,
			name ++ "_amp", amp,
			name ++ "_q", q,
			name ++ "_lfoFreqBus", lfoFreqBus,
			name ++ "_lfoQBus", lfoQBus,
			name ++ "_freqEnv", freqEnv;
			name ++ "_qEnv", qEnv;
			].asOSCArgArray);

	}

}

		