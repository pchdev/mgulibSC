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



j.buffer.loadToFloatArray(action: {|array| a = array});
a;
a.plot;

p = PO_sfPlayer(0, s);
p.generateUI;
p.gain.val = 0.71;
p.saveState("essai");
p.recallState("essai");

p.gain.val;

o = PO_chorusSTS(0, s);
o.generateUI;

(
p = PO_sfPlayer(0, s);
w = Window("basic", Rect(0, 0, 640, 480));
w.background = Color.white;
t = MGU_toggle(w, nil, p.loop);

w.front;
)

p.loop.val;

t.toggle_display_off;
t.toggle_display_on;