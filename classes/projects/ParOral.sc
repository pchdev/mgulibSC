ParOral {

	var <with_gui, <gui;
	var <mic_in, <rec_test, <pre_process;
	var <rack_1, <pshifter, <rmod, <chorus, <delay, <filter_1;
	var <rack_2, <graindelay, <grip, <vocoder;
	var <filter_2, <verb;
	var <minuitInterface;
	var oscFunc;
	var window, mic_toggle, test_toggle, init_button;
	var startPos_slider, initEffects;
	var pre_process_slider;
	var send_slider_array;
	var pre_process_button, rack_1_button, graindelay_button, grip_button, vocoder_button;


	*new { |with_gui = true|
		^this.newCopyArgs(with_gui).init
	}

	init {

		send_slider_array = [];

		"[PARORAL] now building modules...".postln;

		minuitInterface = MGU_minuitInterface("audio", 3127);
		"[PARORAL] minuitInterface succesfully built".postln;

		mic_in = MGU_inModule(name: "mic_in"); "[PARORAL] inModule succesfully built".postln;
		rec_test = PO_sfPlayer(name: "rec_test"); "[PARORAL] rec_test succesfully built".postln;
		pre_process = PO_inProcess(0, name: "pre_process"); "[PARORAL] pre_process succesfully built".postln;
		pre_process.mix.val = 1;
		pre_process.level.val = -24;

		// rack #1

		pshifter = PO_pshifter2(name: "pshifter"); "[PARORAL] pshifter succesfully built".postln;
		pshifter.mix.val = 1;

		rmod = PO_rmod(name: "rmod"); "[PARORAL] rmod succesfully built".postln;
		chorus = PO_chorusMTS(name: "chorus"); "[PARORAL] chorus succesfully built".postln;
		delay = PO_sdelaySTS(name: "delay"); "[PARORAL] delay succesfully built".postln;
		filter_1 = PO_lpf(numInputs: 2, name: "filter_1"); "[PARORAL] filter_1 succesfully built".postln;

		rack_1 = MGU_moduleRack(0, nil, 1, 2, "rack_1"); "[PARORAL] rack1 succesfully built".postln;
		rack_1.mix.val = 1;
		rack_1.addModules(pshifter, rmod, chorus, delay, filter_1);//, chorus, delay, filter_1);

		// others

		//graindelay = PO_granaryMTS(name: "graindelay"); "[PARORAL] graindelay succesfully built".postln;
		grip = PO_grip(name: "grip"); "[PARORAL] grip succesfully built".postln;
		//vocoder = PO_vocoder(name: "vocoder");

		// rack #2

		filter_2 = PO_lpf(name: "filter_2"); "[PARORAL] filter_2 succesfully built".postln;
		verb = PO_zitaSTS(name: "verb"); "[PARORAL] verb succesfully built".postln;

		rack_2 = MGU_moduleRack(name: "rack_2"); "[PARORAL] rack_2 succesfully built".postln;
		rack_2.addModules(filter_2, verb);

		"[PARORAL] building modules completed!".postln;

		// CONNEXIONS

		"[PARORAL] establishing module connexions...".postln;

		mic_in.connectToModule(pre_process);
		rec_test.connectToModule(pre_process);

		pre_process.addNewSend(rack_1);
		//pre_process.addNewSend(graindelay);
		//pre_process.addNewSend(grip);
		//pre_process.addNewSend(vocoder);

		//graindelay.addNewSend(rack_2);
		//grip.addNewSend(rack_2);
		//vocoder.addNewSend(rack_2);

		//rack_1.addNewSend(graindelay);
		//rack_1.addNewSend(grip);

		"[PARORAL] connexions succesfully established!".postln;

		// MINUIT

		"[PARORAL] registering modules to Minuit protocol...".postln;

		mic_in.registerToMinuit(minuitInterface);
		rec_test.registerToMinuit(minuitInterface);
		pre_process.registerToMinuit(minuitInterface);
		rack_1.registerToMinuit(minuitInterface);
		//graindelay.registerToMinuit(minuitInterface);
		//grip.registerToMinuit(minuitInterface);
		//vocoder.registerToMinuit(minuitInterface);
		rack_2.registerToMinuit(minuitInterface);

		"[PARORAL] Minuit registering completed, you may now use i-score.".postln;
		("[PARORAL] Minuit device" + "\"" ++ minuitInterface.address ++ "\"" + "on port" + minuitInterface.port ++ ".").postln;

		// OTHERS

		//rec_test.readFile("samples/lecture-enregistree.wav");
		//rec_test.readFile("/Users/meegooh/Dropbox/ParOral/audio/samples/lecture-enregistree.wav");
		rec_test.readFile("/Users/meegooh/Desktop/lecture_enregistree-mono.wav");

		// GUI

		if(with_gui) {

			AppClock.sched(0, {

				"[PARORAL] building user interface...".postln;
				oscFunc = OSCFunc({|msg| msg.postln}, '/lastIndex', nil, 8889);

				window = Window("ParOral tester", Rect(0, 0, 500, 375), false);
				window.onClose = { rec_test.killAllSynths() };

				startPos_slider = MGU_slider(window, Rect(120, 10, 120, 25), rec_test.startPos);
				test_toggle = MGU_textToggle(window, Rect(10, 10, 100, 25), "tester off", "tester on",
					[{rec_test.killAllSynths()}, {rec_test.sendSynth()}]);
				mic_toggle = MGU_textToggle(window, Rect(10, 40, 100, 25), "mic off", "mic on",
					[{mic_in.killAllSynths}, {mic_in.sendSynth()}]);

				initEffects = MGU_textButton(window, Rect(10, 70, 100, 25), "init effects", {
					rack_1.sendRack();
					pre_process.sendSynth();
					rack_2.sendRack();
				});

				pre_process_slider = MGU_slider(window, Rect(10, 125, 150, 20), pre_process.level, 0);

				pre_process.sendLevelArray.size.do({|i|
					send_slider_array = send_slider_array.add(
						MGU_slider(window, Rect(10, 155 + (i*25), 150, 20),
							pre_process.sendLevelArray[i]));
				});

				// OPEN BUTTONS

				pre_process_button = MGU_textButton(window, Rect(340, 125, 100, 20), "open pre_process", {
					pre_process.generateUI();
				});

				rack_1_button = MGU_textButton(window, Rect(340, 155, 100, 20), "open rack_1", {
					rack_1.generateUI();
				});

				graindelay_button = MGU_textButton(window, Rect(340, 185, 100, 20), "open graindelay", {
					graindelay.generateUI();
				});

				grip_button = MGU_textButton(window, Rect(340, 215, 100, 20), "open grip", {
					grip.generateUI();
				});

				vocoder_button = MGU_textButton(window, Rect(340, 245, 100, 20), "open vocoder", {
					vocoder.generatEUI();
				});

				window.front();
			});

		};

		// PROCESS

		fork {
			1.wait;
			pre_process.sendSynth();
			//rec_test.sendSynth();
		};

	}

}