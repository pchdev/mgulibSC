MGU_synth2 { // builds & launch synth structures on server;

	classvar instanceCount;

	// arg defined attr.
	var <outBus, <server, <rate, <name;

	// private
	var <wrapper, <container, def;
	var <carrier, <rmod, <pmod;
	var <out, <pan, <bal;

	// properties
	var thisInstance, synthGroup, <synthArray;
	var <hasRM, <hasPM;

	*new { |outBus = 0, server, rate = \audio, name|
		^this.newCopyArgs(outBus, server, rate, name).init
	}

	init {

		var out;

		// default-global
		instanceCount !? { instanceCount = instanceCount + 1 };
		instanceCount ?? { instanceCount = 1 };
		thisInstance = instanceCount;

		name ?? { name = "synth_" ++ thisInstance };
		server ?? { server = Server.default };

		// outBus self creation -> when arg < 0
		if(outBus < 0, {
			var bus;
			switch(rate,
				\audio, { bus = Bus.audio(server, 1)},
				\control, { bus = Bus.control(server, 1)});
			outBus = bus.index
		});

		// init subunits
		synthGroup = Group(1);
		container = MGU_container(name, node: synthGroup, oscPort: 3127);

		// init user-parameters
		pan = MGU_parameter(container, \pan, Float,[-1.0, 1.0], 0.0, true);
		out = MGU_parameter(container, \out, Integer, [-inf, inf], outBus, true);
		bal = MGU_parameter(container, \bal, Float, [0.0, 1.0], 1.0, true);
		outBus = out;
		out = nil;
		hasRM = false; hasPM = false;

		// init wrapper
		wrapper = MGU_defWrapper(this, rate);

		// init carrier generator
		carrier = MGU_gen("carrier", server, container, wrapper);
		wrapper.addCarrier(carrier);
		wrapper.wrapDef;

	}

	def {
		^wrapper.def
	}

	// modify synth structure, adding / removing elements

	addRM {
		hasRM = true;
		rmod = MGU_gen("rmod", server, container, wrapper);
		wrapper.addRM(rmod);
		wrapper.refresh;
		rmod.freq.val = 6.0;
		rmod.amp.val = 1.0;
	}

	removeRM {
		hasRM = false;
		rmod = nil;
	}

	addPM { |numModulators = 3|
		pmod = [];
		hasPM = true;
		numModulators.do({|i|
			pmod = pmod.add(MGU_gen("pmod" ++ (i+1), server, container, wrapper));
				wrapper.addPM(pmod.at(i));
		});
	}

	removePM {
		hasPM = false;
		pmod = nil;
		wrapper.removePM;
	}

	// minuit

	registerToMinuit { |minuitInterface|
		minuitInterface.addContainer(container);
		container.parentContainer = minuitInterface;
	}

	// connections

	connectToGen { |gen, param| // if synth is LFO
		if(rate == \audio, { Error("[SYNTHMAKER] ERROR, GEN IS AUDIO RATE").throw });
		outBus.val.postln;
		switch(param,
			\freq, { gen.addLFO_onFreq(outBus.val) },
			\phase, { gen.addLFO_onPhase(outBus.val) },
			\beats, { gen.addLFO_onBeats(outBus.val) },
			\morph, { gen.addLFO_onMorph(outBus.val) });
	}

	// creating / deleting synths

	makeSynth { |optFreq|
		optFreq !? { carrier.freq.val = optFreq };
		synthArray = synthArray.add(
			Synth(name, container.makeSynthArray.asOSCArgArray, synthGroup, 0));
		("Synth created, voice:" + (synthArray.size - 1)).postln;
	}

	killSynth { |voice|
		synthArray[voice].set(\carrier_gate, 0);
		synthArray.removeAt(voice);
	}

	killAll {
		synthArray.size.do({
			synthArray[0].set(\carrier_gate, 0);
			synthArray.removeAt(0);
		});
	}

	voice { |voiceNb| // shortcuts for accessing synthArray
		^synthArray[voiceNb]
	}

	lastVoice {
		("current n° of voices:" + synthArray.size).postln;
		^synthArray[synthArray.size - 1]
	}

}