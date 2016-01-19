
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

