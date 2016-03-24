p = ParOral(true);

(

w = Window("test vu", Rect(0,0, 500, 300), false);

v = MGU_vuMeter(w, Rect(10, 10, 20, 200));
w.front();

)

v.valuePeakRMS(0.5, 0.5);


g = MGU_grainSampler(0, s);


m = MGU_minuitInterface("superColl", 3127);
g = MGU_scGrainBuf();
g.generateUI();
g.pushLearn();


g.registerToMinuit(m);

g.readFile("/Users/meegooh/Desktop/lecture_enregistree-mono.wav");
g.generateUI;
g.sendSynth();
g.killAllSynths();
g.saveState("testpresets2");
g.availablePresets();

z = PO_zitaSTS(num_outputs:2);
z.generateUI();

i = MGU_simplePushInterface();

MIDIFunc.trace;
// cc + = 1 to 10
// cc - = 127 to 117

MGU_moduleGUI

