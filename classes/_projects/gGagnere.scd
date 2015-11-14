// Georges Gagneré



// MINUIT

m = MGU_minuitInterface("superColl", 3127);

// IN + EQ + COMP

p = PO_inProcess("inProc", 0, s);

// RMOD

p = PO_rmod("rmod", 0, s);

// PSHIFTER (doesn't work yet)

p = PO_pShifter("pshift", 0, s);
p.registerToMinuit(m);
p.sendDef;

// ZITA VERB

v = PO_zita("zita", nil, s);

// FILTER

f = PO_filter("filter", nil, s);

// MAX GRANULAR

g = PO_granary("granary", nil, s);

// MANGLER / DOWNSAMPLER

// HARMONIZER

// DISTORTION / WAVESHAPER

// DELAY

p = PO_sdelay("delay", nil, s);

// SF PLAYER

p = PO_sfPlayer("bof", 0, s);
p.readFile();
p.sendSynth;
p.killSynths;
p.loop.val = 0;
p.loop.val;

p.registerToMinuit(m);

// SYNTHESIS

e = MGU_synth2(0, s, \audio, "okay");
e.registerToMinuit(m);

e.makeSynth(200.);
e.rmod.freq.val_(50, e.voice(2), onServ: true);
e.killAll;
e.killSynth(0);
e.def;
e.addRM();

// CONNEXIONS

// example

s.boot;

m = MGU_minuitInterface("superColl", 3127);

~inproc = PO_inProcess("inProc", nil, s);
~pshift = PO_pShifter("pshift", nil, s);
~rmod = PO_rmod("rmod", nil, s);
~chorus = PO_chorus("chrs", nil, s);
~delay = PO_sdelay("delay", nil, s);
~filter1 = PO_filter("filter1", nil, s);
~gain1 = PO_gain("gain1", nil, s);
~master = PO_gain("master", 0, s);

~granary = PO_granary("granary", nil, s);
~grip = PO_grip("grip", nil, s);
~verb = PO_zita("verb", nil, s);


s.boot;
m = MGU_minuitInterface("superColl", 3127);
p = PO_sfPlayer("coll", 0, s);
p.registerToMinuit(m)
p.readFile();
p.sendSynth;

i = PO_inProcess("in", nil, s); // ok !
p = PO_pShifter("pShifter", 0, s); // ok !
r = PO_rmod("rmod", 0, s); // ok !
r.freq.val_(100);
d = PO_sdelay("delay", 0, s); // ok !
c = PO_chorus("chrs", 0, s); // ok !

i.connectToModule(r);
r.connectToModule(c);
c.inbus.val;

i.out;
p.inbus.val;
p.out;

i.sendSynth;
r.sendSynth;
p.sendSynth;
c.sendSynth;

i.connectToModule(p);