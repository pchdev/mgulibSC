



g = MGU_graphDesigner(\effect);
g.addUGen(\osc1, "SinOsc.ar(map_kr, 0, map_kr, 0)");

g.addEnvOnParameter(\osc1, \freq);
g.addLFOOnParameter(\osc1, \freq, 2);
g.processArray;
g.compileGraphFunc(\osc1, false, 0);

g.addUGen(\osc1, SinOsc.ar, [freq, 0, amp, 0]);

SinOsc.ar.rate;

g = MGU_generator(0, s);
g.def;

g.sendSynth;

a = [];
a = a.add(Saw.ar);
a = a.add(LPF.ar(a[0]));

d = SynthDef(\test, {});
d.addUGen(LPF.ar);
d.add;
d.collectConstants;

d.available;
d.children;
d.constants;
d.indexUGens;
d.removeUGen(w);

UGe