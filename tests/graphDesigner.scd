

g = PO_sfPlayer(0, s);
g.recallState("lol");
g.class.instVarNames;

g.playstop.val;

MGU_minuitInterface;


b = BPF.ar;
o = Osc.ar;
o.instVarAt(1);
o.numInputs;
o.argNameForInputAt(2);
o.dumpArgs;

b.numInputs;
b.dumpArgs;


f = MGU_filterMaker(\lol, \bandpass, s, 0, 0);
f.def;

g = MGU_graphDesigner(\effect);

// adds the "In" module : In.ar(inbus.smbKr); + var

g.addUGen(BPF.ar);

// if freq parameter is present in the variables, associate it automatically : instVarNames

// bpf = BPF.ar(in, freq_BPF.smbKr, rq_BPF.smbKr)

g.curUGen.addEnvToInput(\freq, theEnv);

// adds var freq_env = Env.newClear(8).asArray
// -> BPF.ar(in, freq.smbKr * EnvGen.kr(freq_env...);

g.curUGen.addLFOToInput(\rq, lfoBus);

// -> BPF.ar(in, freq.smbKr * EnvGen.kr(freq_env...), rq.smbKr * In.kr

g.closeUGen;
g.addUGen(LPF.ar);

// lpf = LPF.ar(bpf, freq_LPF.smbKr, rq_LPF.smbKr);

g.closeUGen;

g.compileGraph(0);

// if mode = \effect;
// process = in * (1 - drywet) + (lpf * drywet);
// Out.ar(0, process * gain.smbKr);

g = MGU_graphDesigner(\effect);
g.addUGen(BPF.ar);
g.compileGraphFunc;
g.graphDef

f = MGU_graphDesigner(\effect);
f.addUGen(PitchShift.ar);
f.compileGraphFunc;
f.graphDef;

c = ChoruserSC.ar;

d = SynthDef(\poa, g.graphDef.interpret).add;

m = MGU_minuitInterface("superColl", 3127);
c = PO_sfPlayer(0, s);
c.registerToMinuit(m);
c.gain.val = 6;

OSCFunc.trace(false);




