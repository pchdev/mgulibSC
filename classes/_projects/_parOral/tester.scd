i = PO_sfPlayer("jose", nil, s);
p = PO_pShifter("pshift", nil, s);
r = PO_rmod("rmod", 0, s);
c = PO_chorus("chor", nil, s);
d = PO_sdelay("sdelay", 0, s);

i.connectToModule(d);

i.connectToModule(p);
p.connectToModule(r);
r.connectToModule(c);
c.connectToModule(d);

i.readFile("/Users/pchdev/Dropbox/ParOral/_max/lecture-enregistree.wav");
i.sendSynth;
p.sendSynth;
r.sendSynth;
c.sendSynth;
d.sendSynth;

i.out;
p.out;
r.inbus.val;

// pshifter param
p.shift.val = 0.75;
p.mix.val = 1;

// rmod params
r.freq.val =  30;

// chorus param
c.dtime_left.val = 0.05;
c.dtime_left.val;
c.dtime_right.val = 0.5;
c.freq.val = 1;
c.fbk_left.val = 100;
c.fbk_right.val = 100;
c.drywet.val = 0.8;

// sdelay params

d.dtime_left.val = 0.15;
d.dtime_right.val = 0.2;

d.fbk_left.val = 90;
d.fbk_right.val = 10;

// filter params

// grain delay

// grip

// vocoder

// distortion / waveshaping




