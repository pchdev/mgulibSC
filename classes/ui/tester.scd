p = PO_sfPlayer(0, s);
p.generateUI;
p.gain.val = 0.71;
p.saveState("essai");
p.recallState("essai");
p.gain.val = 2;

p.gain.val;

o = PO_chorusSTS(0, s);
o.generateUI;

z = PO_zita(0, s);
z.generateUI;

0.linlin(-1, 1, 0, 1);



a = "jose";
a.drop(-1);
a;



