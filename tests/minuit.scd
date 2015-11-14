// granary -> "jamomize"
// synth -> "jamomize"
// continue development of synth
// finish minuit
// faust -> granary
// lfo child of class synth

m = MGU_minuitInterface("superColl", 3127);

l = MGU_synth2(-1, s, \control, "lfoFreq");
l.connectToGen(d.carrier, \freq);

l.carrier.freq.val_(0.125, true);
l.carrier.amp.val_(0.008, true);
l.makeSynth;

OSCFunc.trace(true);


d = MGU_synth2();
d.makeSynth;

d.def;

d.killSynth(0);
d.registerToMinuit(m);
d.makeSynth(71.midicps);
d.carrier.waveform = \triangle;
d.carrier.morphTo(\sine, d.lastVoice, 3000);
d.carrier.addMorph;
d.carrier.addChorus;
d.wrapper.def
d.carrier.amp.val_(0.5, true);
d.carrier.beats.val_(0.12, true);

d.carrier.freq.val_({|val| val * 1.5}, true, d.lastVoice);
d.carrier.freq.val;

d.carrier.freq.val_(67.midicps, true);
d.carrier.amp.val_(1, true);
d.addRM
d.rmod.freq.val_(45, true);
d.pan.val_(-0.5, true, d.lastVoice);

d.killSynth(0);
d.carrier.def;
d.wrapper.def;
d.container.makeSynthArray;

m.addressList;

m.queryTreeRoot;

d.carrier.container.paramAddresses;
d.rmod.container.paramAddresses;

d.addRM;


m.applicationNodes;
