p = PO_sfPlayer(0, s);
p.generateUI;
p.gain.val = 0.71;
p.saveState("essai");
p.recallState("essai");
p.gain.val = 2;
p.gain.val;

p.gain.val;

o = PO_chorusSTS(0, s);
o.generateUI;

z = PO_zita(0, s);
z.generateUI;


Writing Help
Writing Extensions

// MODULE RACKS

(

p = PO_sfPlayer(0, s);
r = MGU_moduleRack(0, s);
c = PO_chorusSTS(0, s);
r.addModule(p);
r.addModule(c);

)

m = MGU_minuitInterface("superColl", 3127);
r.registerToMinuit(m);
r.generateUI;
n = NetAddr("127.0.0.1", 3127);
n.sendMsg("/moduleRack_2/sfPlayer_1/gain", 2);
p.gain.val;

p.generateUI;

r.module_array[0].container.address;
r.module_array[0].container.contAccesses;
r.module_array[0].gain.address;

r.container.contAccesses[0].address;
r.container.directHierarchy;


// BUTTON
(
w = Window("try", Rect(0, 0, 640, 480));
t = MGU_textButton(w, Rect(100, 100, 200, 100));

w.front;
)
MGU_moduleGUI

// tests

p = PO_sfPlayer(0, s);
p.readFile("/Users/pierrecochard/Dropbox/_projets-scrime/_demo-iscore/iscore-demo3/media/16. Broken hills.wav");
d = PO_zitaSTS(0, s);

p.generateUI;

p.connectToModule(d);

p.sendSynth;
d.sendSynth;

d.generateUI;

o = MGU_cycle(0, s);

m = MGU_minuitInterface
c = MGU_container


p = MGU_cycle(0);
c = PO_chorusSTS();
z = PO_zitaSTS(nil);
r = MGU_moduleRack(0);
r.addModule(p);
r.addModule(z);
r.addModules(p,c,z);
r.module_array;
r.sendSynth;
p.sendSynth

p.out;
z.inbus.val;
z.out;

p.generateUI;

p.waveform.val = \sine;

r.module(1).name;
r.module(0).out;
r.module(1).inbus.val;

r.module(0).name;

m = MGU_minuitInterface("superColl", 3127)
o = MGU_simpleSine(0, s);
o.registerToMinuit(m);
o.sendSynth();
o.level.val = -24;

// out test

fork ({
	inf.do({|i|
		g.freq.val = 50.rrand(2000);
		0.02.wait;
	})
});

g = MGU_simpleSine([0, 1]);
z = PO_zitaSTS(0, s);
g.addNewSend(z);
z.sendSynth;
g.sendSynth;

z.inbus.scope();
g.generateUI;
z.generateUI;

g.out;
z.inbus;
z.inbus.scope;

z.inbus.numChannels;

s.scope;

p = PO_sfPlayer([0, 1]);
z = PO_zitaSTS(0, s);
p.addNewSend(z);
z.sendSynth();

p.readFile("/Users/pierrecochard/Dropbox/_projets-scrime/_demo-iscore/iscore-demo3/media/16. Broken hills.wav");

p.sendSynth();

p.generateUI;
z.generateUI;

r = PO_rmod(0, s);

// EXAMPLES

// 1 - simple

(

p = PO_sfPlayer();
p.readFile("/Users/meegooh/Dropbox/_projets-scrime/_demo-iscore/iscore-demo3/media/16. Broken hills.wav");
p.sendSynth();

)

p.killAllSynths();
p.generateUI; // works okay
p.sendSynth();


// 2 - with connectToModule

p.level.val;
p = PO_sfPlayer();
p.readFile("/Users/meegooh/Dropbox/_projets-scrime/_demo-iscore/iscore-demo3/media/16. Broken hills.wav");
z = PO_zitaSTS();
p.connectToModule(z);
p.out.val;

z.sendSynth()
p.sendSynth();
p.generateUI();
z.generateUI();

p.killAllSynths();
z.killAllSynths(); // works okay

// 3 - with sends

p = PO_sfPlayer();
p.readFile("/Users/meegooh/Dropbox/_projets-scrime/_demo-iscore/iscore-demo3/media/16. Broken hills.wav");
z = PO_zitaSTS();
p.addNewSend(z);
z.sendSynth();
p.sendSynth();
p.generateUI();
z.generateUI();

p.killAllSynths();
z.killAllSynths(); // works okay

// 4 - with instrument rack (witout sends)

p = PO_sfPlayer();
p.readFile("/Users/meegooh/Dropbox/_projets-scrime/_demo-iscore/iscore-demo3/media/16. Broken hills.wav");

c = PO_chorusSTS();
z = PO_zitaSTS();

z.inbus.scope

r = MGU_moduleRack();
r.out.val
r.master_internal;

r.addModules(p,z);

r.sendSynth();


p.sendSynth();
r.generateUI();

p.killAllSynths();
r.killAllSynths();

// 5 - with effect rack (sends)

m = MGU_minuitInterface("superColl", 3127);

p = PO_sfPlayer();
p.readFile("/Users/meegooh/Dropbox/_projets-scrime/_demo-iscore/iscore-demo3/media/16. Broken hills.wav");

c = PO_chorusSTS();
z = PO_zitaSTS();

r = MGU_moduleRack();

r.addModules(z);
p.addNewSend(r);

r.sendRack();
p.sendSynth()

p.generateUI;
r.generateUI();
z.generateUI;

p.killAllSynths();
r.killAllSynths();

// tests

r.inbus;
z.inbus;
z.out();

s.scope();
z.master_internal;
z.master_internal.scope();
r.master_internal;
r.master_internal.scope();
..
// minuit

p.registerToMinuit(m);
r.registerToMinuit(m);
z.registerToMinuit(m);

// granular

g = MGU_glitchyGran();
g.readFile("/Users/meegooh/Dropbox/_projets-scrime/_demo-iscore/iscore-demo3/media/16. Broken hills.wav");
g.readFile();
g.generateUI; // add an alwaysOnTop argument
g.level.val;

f = PO_fShifter(numInputs: 2, numOutputs: 2);
f.generateUI;

z = PO_zitaSTS(0);
z.generateUI(true);

g.connectToModule(f);
f.connectToModule(z);

z.sendSynth();
f.sendSynth();
g.sendSynth();

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

