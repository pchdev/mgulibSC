
// i-score demo
(

k = PO_sfPlayer(0);
k.readFile("/Users/meegooh/Desktop/crystal-glass.wav");
k.loop.val = 0;

g = MGU_glitchyGran();
g.readFile("/Users/meegooh/Dropbox/_projets-scrime/_demo-iscore/iscore-demo3/media/16. Broken hills.wav");

i = MGU_glitchyGran();
i.readFile("/Users/meegooh/Dropbox/_projets-scrime/_demo-iscore/iscore-demo3/media/16. Broken hills.wav");

f = PO_fShifter(numInputs: 2, numOutputs: 2);
z = PO_zitaSTS();
l = MGU_limiter(0, numInputs: 2, numOutputs: 2);

r = MGU_moduleRack(0);
r.addModules(g, f, z, l);

i.addNewSend(z);
)

r.sendRack();
r.generateUI();
i.generateUI();

g.sendSynth();
i.sendSynth();

k.sendSynth();
k.killAllSynths();
k.generateUI();

(
m = MGU_minuitInterface("superColl", 3127);
r.registerToMinuit(m);
k.registerToMinuit(m);
i.registerToMinuit(m)
)

c m= MGU_conversionLib.st_ratio(12);
c;

a = MGU_moduleRack();
a.class.superclass;


// envelope test

u = PO_sampler(0);
u.readFile("/Users/meegooh/Dropbox/_projets-scrime/_demo-iscore/iscore-demo3/media/16. Broken hills.wav");
u.sendSynth();
u.level.kbus.scope
u.env.gate.val = 1;
u.startPos.val = 10000;
u.length.val = 1000;
u.length.val;
u.loop.val = 0;
u.pitch.val = -5;

u.master_internal.scope;

// granary test

p = PO_sfPlayer();
p.readFile("/Users/meegooh/Dropbox/_projets-scrime/_demo-iscore/iscore-demo3/media/16. Broken hills.wav");
g = PO_granaryMTS();

g.registerToMinuit(m);
g.generateUI();

m = MGU_minuitInterface("superColl", 3127);

p.connectToModule(g);

g.connectToModule(z);
g.sendSynth();
p.sendSynth();

g.generateUI();
z.sendSynth();

g.master_internal.scope


z = PO_zitaSTS(0);
z.generateUI();
g.generateUI();


b.bufnum;

{ Phasor.ar(Impulse.ar(1), 1, 0, 1, 0).poll; }.play

Quarks

p = ParOral(true);

f = PO_zitaSTS();
z = PO_chorusSTS();

r = MGU_moduleRack(0);

r.addModules(f,z);

