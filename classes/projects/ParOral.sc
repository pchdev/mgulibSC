ParOral {

	var <with_gui, <gui;
	var <mic_in, <rec_test, <pre_process;
	var <rack_1, <pshifter, <rmod, <chorus, <delay, <filter_1;
	var <rack_2, <graindelay, <grip, <vocoder;
	var <filter_2, <verb;
	var <minuitInterface;


	*new { |with_gui = true|
		^this.newCopyArgs(with_gui).init
	}

	init {

		"[PARORAL]: building modules...".postln;

		minuitInterface = MGU_minuitInterface("audio", 3127);

		mic_in = MGU_inModule(name: "mic_in");
		rec_test = PO_sfPlayer(name: "rec_test");
		pre_process = PO_inProcess(name: "pre_process");

		// rack #1

		rack_1 = MGU_moduleRack(name: "rack_1");

		pshifter = PO_pShifter(name: "pshifter");
		rmod = PO_rmod(name: "rmod");
		chorus = PO_chorusMTS(name: "chorus");
		delay = PO_sdelaySTS(name: "delay");
		filter_1 = PO_lpf(name: "filter_1");

		rack_1.addModules(pshifter, rmod, chorus, delay, filter_1);

		// others

		graindelay = PO_granaryMTS(name: "graindelay");
		grip = PO_grip(name: "grip");
		//vocoder = PO_vocoder(name: "vocoder");

		// rack #2

		rack_2 = MGU_moduleRack(name: "rack_2");

		filter_2 = PO_lpf(name: "filter_2");
		verb = PO_zitaSTS(name: "verb");

		rack_2.addModules(filter_2, verb);

		"[PARORAL] building modules completed!".postln;

		// CONNEXIONS

		"[PARORAL] establishing module connexions...".postln;

		mic_in.connectToModule(pre_process);
		rec_test.connectToModule(pre_process);

		pre_process.addNewSend(rack_1);
		pre_process.addNewSend(graindelay);
		pre_process.addNewSend(grip);
		//pre_process.addNewSend(vocoder);

		graindelay.addNewSend(rack_2);
		grip.addNewSend(rack_2);
		//vocoder.addNewSend(rack_2);

		rack_1.addNewSend(graindelay);
		rack_1.addNewSend(grip);

		"[PARORAL] connexions succesfully established!".postln;

		// MINUIT

		"[PARORAL] registering modules to Minuit protocol...".postln;

		mic_in.registerToMinuit(minuitInterface);
		rec_test.registerToMinuit(minuitInterface);
		pre_process.registerToMinuit(minuitInterface);
		rack_1.registerToMinuit(minuitInterface);
		graindelay.registerToMinuit(minuitInterface);
		//grip.registerToMinuit(minuitInterface);
		//vocoder.registerToMinuit(minuitInterface);
		rack_2.registerToMinuit(minuitInterface);

		"[PARORAL] Minuit registering completed, you may now use i-score.".postln;


		// GUI

		if(with_gui) {
			"[PARORAL] building user interface...".postln;
		};







	}

}