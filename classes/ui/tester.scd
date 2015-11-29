(
w = Window("bleurfp", Rect(0, 0, 640, 480));
w.background = Color.white;

b = MGU_drumM_beatToggle(w, nil, nil);

w.front;
)


(

j = PO_sfPlayer(0, s);
j.readFile("/Users/meegooh/Dropbox/_projets-scrime/_demo-iscore/iscore-demo3/media/16. Broken hills.wav");
w = Window("basic", Rect(0, 0, 640, 480));
w.background = Color.white;



)


p = PO_sfPlayer(0, s);
p.generateUI;
p.gain.val = 0.71;
p.saveState("essai");

o = PO_chorusSTS(0, s);
o.generateUI;