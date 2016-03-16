p = ParOral(true);


(
p = PO_sfPlayer(name: "rec_test");
p.readFile("/Users/meegooh/Desktop/lecture_enregistree-mono.wav");

h = PO_pshifter2(0);

p.connectToModule(h);

)

h.sendSynth();
p.sendSynth();

f = MGU_grainSampler(0,s);
f.readFile();
f.sendSynth();
f.generateUI();

g = MGU_grainSampler(0,s);
g.buf_env.plot;
g.readFile("/Users/meegooh/Desktop/lecture_enregistree-mono.wav");
g.readFile("/Users/meegooh/Desktop/_compo-JIM/stem_1.wav");
g.killAllSynths();
g.sendSynth();
g.generateUI();
g.saveState("testgran");
g.recallState("testgran");

z = PO_zitaSTS(0,s);
z.sendSynth();

g.connectToModule(z);
z.generateUI();

g.grainSize.val = 1000

g.grainSize.val(true);
g.freq.val;


(
d = SynthDef(\testRamper, { var process, ramp;
	process = MGU_steadyClock.ar(\freq.kr);
	Out.ar(0, process)
}).add;
)
(
y = Synth(\testRamper, [\freq, 1]);
s.scope()
)
(
y.set(\freq, 1);
s.scope;
)
Writing Unit Generators





