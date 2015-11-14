


m = MGU_minuitInterface(\superColl, 3127);

c = MGU_chorus(1, 0, "chorus1");
c.makeOnServ


c.bal.defaultNode;
c.bal.default;
c.bal.val;
c.bal.alwaysOnServ;
c.registerToMinuit(m);
f = MGU_sfPlayer(nil, 0, s, \test);
f.registerToMinuit(m);
f.readFile("/Users/pchdev/Desktop/_mix/Unknown Album/03. Spherical field.aif");

d = MGU_delay(1, 0, "delay1");
d.registerToMinuit(m);



f.startPos.val_(1500);
f.stopPos.val_(3000);
f.play;

f.loop.val_(1);
f.numLoops;