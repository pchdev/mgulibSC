(
w = Window("bleurfp", Rect(0, 0, 640, 480));
w.background = Color.white;

b = MGU_drumM_beatToggle(w, nil, nil);

w.front;
)


(

j = PO_sfPlayer(0, s);
w = Window("basic", Rect(0, 0, 640, 480));
w.background = Color.white;

b = MGU_slider(w, Rect(20, 20, 150, 20), j.loop);
w.front;

)

j.gain.val;

p = PO_sfPlayer(0, s);
p.gain;
p.gain.val;
p.startPos;


p.gain = 4;
p.gain;
p.gain.address.class;
p.readFile();
p.sendSynth;
p.gain.val = 0.5;

"3".interpret.class == Integer;

"4".isInteger;

Char













