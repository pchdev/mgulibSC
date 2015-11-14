
f = PO_sfPlayer(nil, s);

m = MGU_minuitInterface("superColl", 3127);

p.registerToMinuit(m);

p = PO_multiEQ(0, s);

p.addBand(PO_lpf(0, s), 2000);
p.addBand(PO_hpf(0, s), 100);

p.freq.val;

p.band.size;
p.band[0].freq.val;
p.band[1].freq.val;


p.band[0].inbus.val;

p.band[0].out;
p.band[1].inbus.val;