// left to do

// main interface + vu meters
// limiter ?
// master eq + multi-band compressor ?
// granary + grip


// inputs

~entree_micro = MGU_inModule(name: "entree_mic");
~rec_test = PO_sfPlayer(name: "rec_test");

// pre-processing

~pre_process = PO_inProcess(0, name: "in_pre-process");

// rack #1

~rack1 = MGU_moduleRack(0);

~pshifter = PO_pShifter(name: "pshifter");
~rmod = PO_rmod(name: "rmod");
~chorus = PO_chorusMTS(name: "chrs");
~dly = PO_sdelaySTS(name: "dly");
~filter1 = PO_lpf(name: "lpf1");

~rack1.addModules(~pshifter, ~rmod, ~chorus, ~dly, ~filter1);

// gran delay vocoder

~grn = PO_granary();
~grip = PO_grip();
~voc = PO_vocoder();


// rack #2

~rack2 = MGU_moduleRack(0);
~filter2 = PO_lpf(name: "lpf2");
~vrb = PO_zitaSTS(name: "vrb");

~rack2.addModules(~filter2, ~vrb);

///////////////////////////////////

// CONNEXIONS

///////////////////////////////////


~entree_micro.connectToModule(~pre_process);
~rec_test.connectToModule(~pre_process);

~pre_process.addNewSend(~rack1);
~pre_process.addNewSend(~grn);
~pre_process.addNewSend(~grip);

~grn.addNewSend(~rack2);
~grip.addNewSend(~rack2);


DynKlank
