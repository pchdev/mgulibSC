ParOral {

	var <with_gui, <gui;
	var <mic_in, <rec_test, <pre_process;
	var <rack_1, <pshifter, <rmod, <chorus, <delay, <filter_1;
	var <rack_2, <graindelay, <vocoder;
	var <filter_2, <verb;
	var <out_limiter, <voice_analyzer, <panner;
	var <minuitInterface;
	var oscFunc;
	var window, mic_toggle, test_toggle, init_button;
	var startPos_slider, initEffects;
	var pre_process_slider;
	var send_slider_array;
	var pre_process_button, rack_1_button, graindelay_button, grip_button, vocoder_button;
	var voice_analyzer_button, limiter_button, rack_2_button;

	var <boiling_sample, paatos_sample;


	*new { |with_gui = true|
		^this.newCopyArgs(with_gui).init
	}

	init {

		send_slider_array = [];

		"[PARORAL] now building modules...".postln;

		minuitInterface = MGU_minuitInterface("audio", 3127);
		"[PARORAL] minuitInterface succesfully built".postln;

		// sound_design

		boiling_sample = PO_sfPlayer(name: "boiling_sample");
		boiling_sample.sends_only = true;

		paatos_sample = PO_sfPlayer(name: "paatos_sample");

		boiling_sample.readFile("/Users/meegooh/Dropbox/ParOral/audio/samples/nappe-intro.wav");
		paatos_sample.readFile("/Users/meegooh/Dropbox/ParOral/audio/samples/paatos.wav");

		// in + pre-processing

		mic_in = MGU_inModule(name: "mic_in");
		"[PARORAL] inModule succesfully built".postln;

		rec_test = PO_sfPlayer(name: "rec_test");
		"[PARORAL] rec_test succesfully built".postln;

		pre_process = PO_inProcess(name: "pre_process");
		"[PARORAL] pre_process succesfully built".postln;
		pre_process.mix.val = 1;
		pre_process.level.val = -24;

		panner = MGU_pan2(name: "panner");
		panner.sends_only = true;
		panner.mix.val = 1;

		// rack #1

		pshifter = PO_pshifter2(name: "pshifter");
		"[PARORAL] pshifter succesfully built".postln;
		pshifter.mix.val = 1;

		rmod = PO_rmod(name: "rmod");
		"[PARORAL] rmod succesfully built".postln;

		chorus = PO_chorusMTS(name: "chorus");
		"[PARORAL] chorus succesfully built".postln;

		delay = PO_sdelaySTS(name: "delay");
		"[PARORAL] delay succesfully built".postln;

		filter_1 = PO_lpf(num_inputs: 2, name: "filter_1");
		filter_1.mix.val = 0;
		"[PARORAL] filter_1 succesfully built".postln;

		rack_1 = MGU_moduleRack(num_inputs: 1, num_outputs: 2, name: "rack_1");
		"[PARORAL] rack_1 succesfully built".postln;

		rack_1.description = "input > rmod > chorus > delay > lpf > output";
		rack_1.mix.val = 1;
		rack_1.addModules(pshifter, rmod, chorus, delay, filter_1);//, chorus, delay, filter_1);

		// others

		graindelay = MGU_grainDelay(name: "grain_delay");
		"[PARORAL] graindelay succesfully built".postln;

		// rack #2

		filter_2 = PO_lpf(name: "filter_2", num_inputs: 2, num_outputs: 2);
		"[PARORAL] filter_2 succesfully built".postln;

		verb = PO_zitaSTS(name: "verb");
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
		voice_analyzer.thresh.val = -12;
		"[PARORAL] voice_analyzer successfully built".postln;

		// CONNEXIONS

		"[PARORAL] establishing module connexions...".postln;

		//boiling_sample.connectToModule(out_limiter);
		boiling_sample.addNewSend(out_limiter);
		boiling_sample.addNewSend(rack_2);

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
		pre_process.sendlevel_array[2].val = -96;
		panner.sendlevel_array[0].val = -96;

		//pre_process.addNewSend(vocoder);

		graindelay.connectToModule(out_limiter);
		graindelay.addNewSend(rack_2);

		//vocoder.connectToModule(out_limiter);
		//vocoder.addNewSend(rack_2);

		rack_1.connectToModule(out_limiter);
		rack_1.addNewSend(graindelay);
		rack_1.addNewSend(rack_2);

		rack_2.connectToModule(out_limiter);

		"[PARORAL] connexions succesfully established!".postln;

		// MINUIT

		"[PARORAL] registering modules to Minuit protocol...".postln;

		boiling_sample.registerToMinuit(minuitInterface);
		paatos_sample.registerToMinuit(minuitInterface);

		mic_in.registerToMinuit(minuitInterface);
		rec_test.registerToMinuit(minuitInterface);
		pre_process.registerToMinuit(minuitInterface);
		rack_1.registerToMinuit(minuitInterface);
		graindelay.registerToMinuit(minuitInterface);

		//vocoder.registerToMinuit(minuitInterface);
		rack_2.registerToMinuit(minuitInterface);
		voice_analyzer.registerToMinuit(minuitInterface);
		panner.registerToMinuit(minuitInterface);
		out_limiter.registerToMinuit(minuitInterface);

		"[PARORAL] Minuit registering completed, you may now use i-score.".postln;
		("[PARORAL] Minuit device" + "\"" ++ minuitInterface.address
			++ "\"" + "on port" + minuitInterface.port ++ ".").postln;

		// SAMPLES BUFFERING

		//rec_test.readFile("samples/lecture_enregistree-mono.wav");
		//boiling_sample.readFile("samples/nappe-intro.wav");
		//paatos_sample.readFile("samples/paatos.wav");

		rec_test.readFile("/Users/meegooh/Desktop/lecture_enregistree-mono.wav");

		// GUI

		if(with_gui) {

			var buttons_offset = 560;

			AppClock.sched(0, {

				"[PARORAL] building user interface...".postln;
				//oscFunc = OSCFunc({|msg| msg.postln}, '/lastIndex', nil, 8889);

				window = Window("ParOral tester", Rect(0, 0, 700, 375), false);
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

				// MINUIT INFORMATION FOOTER

				StaticText(window, Rect(10, 360, 200, 15))
				.font_(Font("Arial", 10))
				.string_("Minuit device \"" ++ minuitInterface.address
					++ "\" on port" + minuitInterface.port ++ ".");

				// MISC.

				StaticText(window, Rect(545, 360, 300, 15))
				.font_(Font("Arial", 10, false, true))
				.string_("ParOral testing window - pchd");

				window.front();

			});

		};

		// PROCESS

	}

}