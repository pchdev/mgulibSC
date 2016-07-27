ParOral {

	var <with_gui, <gui;
	var <mic_in, <rec_test, <pre_process;
	var <rack_1, <pshifter, <rmod, <chorus, <delay, <filter_1;
	var <rack_2, <graindelay, <vocoder;
	var <filter_2, <verb;
	var <out_limiter, <voice_analyzer, <panner;
	var <index_trigger;
	var <minuitInterface;
	var oscFunc;
	var window, mic_toggle, test_toggle, init_button;
	var startPos_slider, initEffects;
	var pre_process_slider;
	var send_slider_array;
	var pre_process_button, rack_1_button, graindelay_button, grip_button, vocoder_button;
	var voice_analyzer_button, limiter_button, rack_2_button;

	var <boiling_sample, <paatos_sample;
	var <streetambient_sample, <churchbells1_sample, <churchbells2_sample;
	var <firecrackers_sample;
	var <cicadas_sample, <cricket_sample, <cricketswarm1_sample, <cricketswarm2_sample;
	var <carriage_sample, <singing_sample;

	var <george_darkchoir_sample, <nightmare_cicadas_sample;
	var <strange_light_sample, <strange_light2_sample, <gripFX_sample;
	var <knock_sample;
	var event_array, current_event;


	*new { |with_gui = true|
		^this.newCopyArgs(with_gui).init
	}

	init {

		event_array = [0, 1, 312, 505, 834, 1100, 1200, 1500, 1550, 1830, 2090];
		current_event = 0;

//		MGU_simplePushInterface();

		send_slider_array = [];

		"[PARORAL] now building modules...".postln;

		minuitInterface = MGU_minuitInterface("audio", 9998, true);
		//minuitInterface.respAddr_("192.168.0.2", 13579);
		"[PARORAL] minuitInterface succesfully built".postln;

		// sound_design

		boiling_sample = PO_sfPlayer(name: "boiling_sample");
		boiling_sample.sends_only = true;

		paatos_sample = PO_sfPlayer(name: "paatos_sample");

		rec_test = PO_sfPlayer(name: "rec_test");
		"[PARORAL] rec_test succesfully built".postln;

		knock_sample = PO_sfPlayer(name: "knock_sample");

		strange_light_sample = PO_sfPlayer(name: "strangelight_sample");
		strange_light_sample.description = "strange light synthesis";

		churchbells1_sample = PO_sfPlayer(name: "churchbells1_sample");
		churchbells1_sample.description = "big church bells";

		churchbells2_sample = PO_sfPlayer(name: "churchbells2_sample");
		churchbells2_sample.description = "lighter church bells ambience";

		streetambient_sample = PO_sfPlayer(name: "streetambient_sample");
		streetambient_sample.description = "street voices";

		firecrackers_sample = PO_sfPlayer(name: "firecrackers_sample");
		firecrackers_sample.description = "distant reverberated firecrackers sample";

		cicadas_sample = PO_sfPlayer(name: "cicadas_sample");
		cicadas_sample.description = "a swarm of cicadas";

		cricket_sample = PO_sfPlayer(name: "cricket_sample");
		cricket_sample.description = "single cricket";

		cricketswarm1_sample = PO_sfPlayer(name: "cricketswarm1_sample");
		cricketswarm1_sample.description = "cricket swarm n°1";

		cricketswarm2_sample = PO_sfPlayer(name: "cricketswarm2_sample");
		cricketswarm2_sample.description = "cricket swarm n°2";

		carriage_sample = PO_sfPlayer(name: "carriage_sample");
		carriage_sample.description = "carriage noise";

		singing_sample = PO_sfPlayer(name: "singing_sample");
		singing_sample.description = "distant bar singing";

		gripFX_sample = PO_sfPlayer(name: "gripFX_sample");
		strange_light2_sample = PO_sfPlayer(name: "strangelight2_sample");


		Platform.case(

			\osx, {

				knock_sample
				.readFile("/Users/meegooh/Dropbox/ParOral/audio/paroral_samples/knockrev.wav");

				gripFX_sample
				.readFile("/Users/meegooh/Dropbox/ParOral/audio/paroral_samples/gripFX.wav");

				strange_light2_sample
				.readFile("/Users/meegooh/Dropbox/ParOral/audio/paroral_samples/strange_light2.wav");

				boiling_sample
				.readFile("/Users/meegooh/Dropbox/ParOral/audio/paroral_samples/nappe-intro.wav");

				paatos_sample
				.readFile("/Users/meegooh/Dropbox/ParOral/audio/paroral_samples/paatos.wav");

				churchbells1_sample
				.readFile("/Users/meegooh/Dropbox/ParOral/audio/paroral_samples/cloches1.wav");

				streetambient_sample
				.readFile("/Users/meegooh/Dropbox/ParOral/audio/paroral_samples/ambiance-voix-rue.wav");

				churchbells2_sample
				.readFile("/Users/meegooh/Dropbox/ParOral/audio/paroral_samples/cloches2.wav");

				firecrackers_sample
				.readFile("/Users/meegooh/Dropbox/ParOral/audio/paroral_samples/petards1.wav");

				cicadas_sample
				.readFile("/Users/meegooh/Dropbox/ParOral/audio/paroral_samples/cicadas1.wav");

				cricket_sample
				.readFile("/Users/meegooh/Dropbox/ParOral/audio/paroral_samples/cricket1.wav");

				cricketswarm1_sample
				.readFile("/Users/meegooh/Dropbox/ParOral/audio/paroral_samples/cricketswarm1.wav");

				cricketswarm2_sample
				.readFile("/Users/meegooh/Dropbox/ParOral/audio/paroral_samples/grillons2.wav");

				carriage_sample
				.readFile("/Users/meegooh/Dropbox/ParOral/audio/paroral_samples/carriage1.wav");

				singing_sample
				.readFile("/Users/meegooh/Dropbox/ParOral/audio/paroral_samples/singing1.wav");

				strange_light_sample
				.readFile("/Users/meegooh/Dropbox/ParOral/audio/paroral_samples/strange_light.wav");

				rec_test.readFile("/Users/meegooh/Desktop/lecture_enregistree-mono.wav")},

			\linux, {
				boiling_sample.readFile("/home/fluxus/Bureau/paroral_samples/nappe-intro.wav");
				paatos_sample.readFile("/home/fluxus/Bureau/paroral_samples/paatos.wav");
				rec_test.readFile("/home/fluxus/Bureau/paroral_samples/lecture_enregistree-mono.wav")};
		);

		// in + pre-processing

		mic_in = MGU_inModule(name: "mic_in");
		"[PARORAL] inModule succesfully built".postln;

		pre_process = PO_inProcess(name: "pre_process");
		"[PARORAL] pre_process succesfully built".postln;
		pre_process.mix.val = 0;
		pre_process.level.val = -24;
		pre_process.level.pushLearn(71);

		panner = MGU_pan2(name: "panner");
		panner.sends_only = true;
		panner.mix.val = 1;

		// others

		graindelay = MGU_grainDelay2(name: "grain_delay");
		graindelay.mix.val = 1;
		"[PARORAL] graindelay succesfully built".postln;

		// rack #1

		pshifter = PO_pshifter2(name: "pshifter");
		pshifter.mix.val = 0;
		"[PARORAL] pshifter succesfully built".postln;

		rmod = PO_rmod(name: "rmod");
		rmod.mix.val = 0;
		"[PARORAL] rmod succesfully built".postln;

		chorus = PO_chorusMTS(name: "chorus");
		chorus.mix.val = 0;
		"[PARORAL] chorus succesfully built".postln;

		delay = PO_sdelaySTS(name: "delay");
		delay.mix.val = 0;
		"[PARORAL] delay succesfully built".postln;

		filter_1 = PO_lpf(num_inputs: 2, name: "filter_1");
		filter_1.mix.val = 0;
		"[PARORAL] filter_1 succesfully built".postln;

		rack_1 = MGU_moduleRack(num_inputs: 1, num_outputs: 2, name: "rack_1");
		"[PARORAL] rack_1 succesfully built".postln;

		rack_1.description = "input > rmod > chorus > delay > lpf > output";
		rack_1.mix.val = 1;
		rack_1.addModules(pshifter, rmod, chorus, delay, filter_1);//, chorus, delay, filter_1);


		// rack #2

		filter_2 = PO_lpf(name: "filter_2", num_inputs: 2, num_outputs: 2);
		filter_2.mix.val = 0;
		"[PARORAL] filter_2 succesfully built".postln;

		verb = PO_zitaSTS(name: "verb");
		verb.mix.val = 1;
		"[PARORAL] verb succesfully built".postln;

		rack_2 = MGU_moduleRack(name: "rack_2");
		"[PARORAL] rack_2 succesfully built".postln;

		rack_2.description = "input > filter > verb > output";
		rack_2.addModules(filter_2, verb);
		rack_2.mix.val = 1;

		"[PARORAL] module building completed!".postln;

		// final limiter

		out_limiter = MGU_limiter(num_inputs: 2, num_outputs: 2, name: "master_limiter");
		out_limiter.mix.val = 1;
		"[PARORAL] limiter succesfully built".postln;

		// voice analysis

		voice_analyzer = PO_voiceAnalyzer(name: "voice_analyzer");
		voice_analyzer.thresh.val = -22;
		"[PARORAL] voice_analyzer successfully built".postln;

		// CONNEXIONS

		"[PARORAL] establishing module connexions...".postln;

		//boiling_sample.connectToModule(out_limiter);
		boiling_sample.addNewSend(out_limiter);
		boiling_sample.addNewSend(rack_2);

		streetambient_sample.connectToModule(out_limiter);
		churchbells1_sample.connectToModule(out_limiter);
		churchbells2_sample.connectToModule(out_limiter);
		firecrackers_sample.connectToModule(out_limiter);
		cicadas_sample.connectToModule(out_limiter);
		cricket_sample.connectToModule(out_limiter);
		cricketswarm1_sample.connectToModule(out_limiter);
		cricketswarm2_sample.connectToModule(out_limiter);
		carriage_sample.connectToModule(out_limiter);
		singing_sample.connectToModule(out_limiter);

		strange_light_sample.connectToModule(out_limiter);
		strange_light2_sample.connectToModule(out_limiter);
		gripFX_sample.connectToModule(out_limiter);
		knock_sample.connectToModule(out_limiter);

		paatos_sample.connectToModule(out_limiter);

		mic_in.connectToModule(pre_process);
		rec_test.connectToModule(pre_process);

		pre_process.connectToModule(panner);
		pre_process.addNewSend(rack_1);
		pre_process.addNewSend(voice_analyzer);
		pre_process.addNewSend(graindelay);

		panner.addNewSend(rack_2);
		panner.addNewSend(out_limiter);

		pre_process.sendlevel_array[0].val = -96;
		pre_process.sendlevel_array[1].val = 6;
		pre_process.sendlevel_array[0].pushLearn(72);
		pre_process.sendlevel_array[2].val = -96;
		pre_process.sendlevel_array[2].pushLearn(73);

		panner.sendlevel_array[0].val = -96;
		panner.sendlevel_array[0].pushLearn(74);
		panner.sendlevel_array[1].pushLearn(75);

		//pre_process.addNewSend(vocoder);

		graindelay.connectToModule(out_limiter);
		graindelay.addNewSend(rack_2);
		graindelay.sendlevel_array[0].val = -96;

		//vocoder.connectToModule(out_limiter);
		//vocoder.addNewSend(rack_2);

		rack_1.connectToModule(out_limiter);
		rack_1.addNewSend(graindelay);
		rack_1.addNewSend(rack_2);

		rack_2.connectToModule(out_limiter);

		"[PARORAL] connexions succesfully established!".postln;

		// MINUIT

		"[PARORAL] registering modules to Minuit protocol...".postln;

		index_trigger = MGU_ParOralTrigger(name: "trigger");

		this.instVarSize.do({|i|
			if(this.instVarAt(i).class.superclasses.includes(MGU_AbstractModule))
				{ this.instVarAt(i).registerToMinuit(minuitInterface) }
		});

		"[PARORAL] Minuit registering completed, you may now use i-score.".postln;
		("[PARORAL] Minuit device" + "\"" ++ minuitInterface.address
			++ "\"" + "on port" + minuitInterface.port ++ ".").postln;

		MIDIFunc.cc({|v, num|
			if(v == 0) {
				current_event = current_event + 1;
				index_trigger.lastIndex.val = event_array[current_event - 1];
			};
		}, 45);

		MIDIFunc.cc({|v, num|
			if(v == 0) {
				current_event = (current_event - 1).clip(0, 1000);
				index_trigger.lastIndex.val = event_array[current_event].asInteger;
			};
		}, 44);

		MIDIFunc.cc({|v, num|
			if(v == 0) {
				current_event = 0;
				index_trigger.lastIndex.val = 0;
			};
		}, 47);

		MIDIFunc.noteOn({|vel, note|
			switch(note)
			{92} { index_trigger.scene.val = 1 }
			{93} { index_trigger.scene.val = 2 }
			{94} { index_trigger.scene.val = 3 }
			{95} { index_trigger.scene.val = 4 }
		});

		// GUI

		if(with_gui) {

			var buttons_offset = 560;

			AppClock.sched(0, {

				"[PARORAL] building user interface...".postln;
				//oscFunc = OSCFunc({|msg| msg.postln}, '/lastIndex', nil, 8889);

				window = Window("ParOral tester", Rect(0, 0, 700, 500), false);
				window.background = Color.white;
				window.onClose = { rec_test.killAllSynths() };

				initEffects = MGU_textButton(window, Rect(10, 10, 100, 25),
					"init effects", {
						out_limiter.sendSynth();
						panner.sendSynth();
						graindelay.sendSynth();
						rack_2.sendRack();
						rack_1.sendRack();
						voice_analyzer.sendSynth();
						pre_process.sendSynth();
				});

				mic_toggle = MGU_textToggle(window, Rect(109, 10, 100, 25),
					"mic off", "mic on",
					[{mic_in.killAllSynths}, {mic_in.sendSynth()}]);

				test_toggle = MGU_textToggle(window, Rect(208, 10, 100, 25),
					"tester off", "tester on",
					[{rec_test.killAllSynths()}, {rec_test.sendSynth()}]);

				MGU_textToggle(window, Rect(307, 10, 100, 25),
					"tester pause off", "tester pause on",
					[{rec_test.pause.val = 0}, {rec_test.pause.val = 1}]);

				MGU_textToggle(window, Rect(10, 34, 100, 25),
					"sample-intro off", "sample-intro on",
					[{boiling_sample.killAllSynths()}, {boiling_sample.sendSynth()}]);

				MGU_textToggle(window, Rect(109, 34, 100, 25),
					"paatos off", "paatos on",
					[{paatos_sample.killAllSynths()}, {paatos_sample.sendSynth()}]);


				MGU_hSeparator(window, Rect(10, 80, 690, 1));

				startPos_slider = MGU_slider(window, Rect(10, 100, 150, 20), rec_test.startPos)
				.background_color_(MGU_colorPalette.blueGreenGrey());


				pre_process_slider = MGU_slider(window, Rect(10, 125, 150, 20),
					pre_process.level, 0).background_color_(MGU_colorPalette.blueGreenGrey_2());

				pre_process.sendlevel_array.size.do({|i|
					var slider_color;
					if(i%2 == 0)
					{ slider_color = MGU_colorPalette.blueGreenGrey() }
					{ slider_color = MGU_colorPalette.blueGreenGrey_2() };
					send_slider_array = send_slider_array.add(
						MGU_slider(window, Rect(10, 150 + (i*25), 150, 20),
							pre_process.sendlevel_array[i])
						.background_color_(slider_color));
				});

				MGU_slider(window, Rect(10, 250, 150, 20), panner.sendlevel_array[0])
				.background_color_(MGU_colorPalette.blueGreenGrey());

				MGU_slider(window, Rect(10, 275, 150, 20), panner.sendlevel_array[1])
				.background_color_(MGU_colorPalette.blueGreenGrey_2());

				// OPEN BUTTONS

				pre_process_button = MGU_textButton(window, Rect(buttons_offset, 125, 100, 20),
					"open pre_process", {pre_process.generateUI()});

				rack_1_button = MGU_textButton(window, Rect(buttons_offset, 150, 100, 20),
					"open rack_1", {rack_1.generateUI()});

				voice_analyzer_button = MGU_textButton(window, Rect(buttons_offset, 175, 100, 20),
					"open voice analyzer", {voice_analyzer.generateUI()});

				graindelay_button = MGU_textButton(window, Rect(buttons_offset, 200, 100, 20),
					"open graindelay", {graindelay.generateUI()});

				vocoder_button = MGU_textButton(window, Rect(buttons_offset, 225, 100, 20),
					"open vocoder", {vocoder.generateUI()});

				rack_2_button = MGU_textButton(window, Rect(buttons_offset, 250, 100, 20),
					"open rack_2", { rack_2.generateUI() });

				limiter_button = MGU_textButton(window, Rect(buttons_offset, 275, 100, 20),
					"open limiter", {out_limiter.generateUI()});

				MGU_textButton(window, Rect(buttons_offset, 300, 100, 20),
					"open sample-intro", {boiling_sample.generateUI()});

				MGU_textButton(window, Rect(buttons_offset, 325, 100, 20),
					"open paatos", {paatos_sample.generateUI()});

				MGU_textButton(window, Rect(buttons_offset, 350, 100, 20),
					"open lastIndex", {index_trigger.generateUI()});

				MGU_textButton(window, Rect(buttons_offset, 375, 100, 20),
					"open street-ambient", {streetambient_sample.generateUI()});

				MGU_textButton(window, Rect(buttons_offset, 400, 100, 20),
					"open churchbells_1", {churchbells1_sample.generateUI()});

				MGU_textButton(window, Rect(buttons_offset, 425, 100, 20),
					"open churchbells_2", {churchbells2_sample.generateUI()});

				MGU_textButton(window, Rect(buttons_offset, 450, 100, 20),
					"open firecrackers", {firecrackers_sample.generateUI()});

				// MINUIT INFORMATION FOOTER

				StaticText(window, Rect(10, 485, 200, 15))
				.font_(Font("Arial", 10))
				.string_("Minuit device \"" ++ minuitInterface.address
					++ "\" on port" + minuitInterface.port ++ ".");

				// MISC.

				StaticText(window, Rect(545, 485, 300, 15))
				.font_(Font("Arial", 10, false, true))
				.string_("ParOral testing window - pchd");

				window.front();

			});

		};

		// PROCESS

	}

}