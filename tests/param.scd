

h = MGU_hub("main");
h.address;

p = MGU_parameter(h, \testFloat, Float, [0.0, 1.0], \both, 0.5);

p.hub;
p.val;
p.address;
p.defName;
p.defNameKr;
p.name
p.val = 1.5;

a = h.makeSynthArray;


