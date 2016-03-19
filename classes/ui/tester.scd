p = ParOral(true);

(

w = Window("test vu", Rect(0,0, 500, 300), false);

v = MGU_vuMeter(w, Rect(10, 10, 20, 200));
w.front();

)

v.valuePeakRMS(0.5, 0.5);

g = MGU_scGrainBuf()
g.readFile("/Users/meegooh/Desktop/lecture_enregistree-mono.wav");
g.generateUI;