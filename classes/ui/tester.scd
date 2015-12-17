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



