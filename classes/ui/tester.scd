p = ParOral(true);


(
p = PO_sfPlayer(name: "rec_test");
p.readFile("/Users/meegooh/Desktop/lecture_enregistree-mono.wav");

h = PO_pshifter2(0);

p.connectToModule(h);

)

h.sendSynth();
p.sendSynth();

p.generateUI();
h.generateUI();

{ MGU_ramper.ar(440) }.play;
s.scope




