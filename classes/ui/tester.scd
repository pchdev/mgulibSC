p = ParOral(true);

(

w = Window("test vu", Rect(0,0, 500, 300), false);

v = MGU_vuMeter(w, Rect(10, 10, 20, 200));
w.front();

)

v.valuePeakRMS(0.5, 0.5);

g = MGU_scGrainBuf();
g.readFile("/Users/meegooh/Desktop/lecture_enregistree-mono.wav");
g.generateUI;
g.sendSynth();
g.killAllSynths();
g.saveState("testpresets2");
g.availablePresets();


(
w = Window("test", Rect(0, 0, 640, 480));
w.background = Color.white;
p = PopUpMenu(w, Rect(10, 10, 100, 20));
p.items = g.availablePresets();
p.font = Font("Arial", 11);

w.front();

)