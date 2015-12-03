p = PO_sfPlayer(0, s);
p.generateUI;
p.gain.val = 0.71;
p.saveState("essai");
p.recallState("essai");
p.gain.val = 2;
p.gain.val;

p.gain.val;

o = PO_chorusSTS(0, s);
o.generateUI;

z = PO_zita(0, s);
z.generateUI;


// MODULE RACKS

(

p = PO_sfPlayer(0, s);
r = MGU_moduleRack(0, s);
r.addModule(p);

)

m = MGU_minuitInterface("superColl", 3127);
r.registerToMinuit(m);

n = NetAddr("127.0.0.1", 3127);
n.sendMsg("/moduleRack_2/sfPlayer_1/gain", 2);
p.gain.val;

p.generateUI;

r.module_array[0].container.address;
r.module_array[0].container.contAccesses;
r.module_array[0].gain.address;

r.container.contAccesses[0].address;
r.container.directHierarchy;



