(
w = Window("bleurfp", Rect(0, 0, 640, 480));
w.background = Color.white;

b = MGU_drumM_beatToggle(w, nil, nil);

w.front;
)

(

j = PO_sfPlayer(0, s);
w = Window("bleurfp", Rect(0, 0, 640, 480));
w.background = Color.white;

b = MGU_slider(w, Rect(20, 20, 150, 20), nil);
b.bindToParameter(j.gain);

w.front;

)

(

j = PO_sfPlayer(0, s);
w = Window("basic", Rect(0, 0, 640, 480));
w.background = Color.white;

b = MGU_slider2(w, Rect(20, 20, 150, 20), j.gain);
w.front;

)

p = PO_sfPlayer(0, s);
p.readFile();
p.sendSynth;
p.gain.val = 0.5;

a = -6;
a = a.neg;













