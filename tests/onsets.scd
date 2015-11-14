(
// onset detection

n = NetAddr("127.0.0.1", 8888); // sending to Max
c = OSCFunc({ |msg| msg.postln; n.sendMsg("/onset", msg[3])}, '/onset');

f = PO_sfPlayer(0);
f.readFile("/Users/pchdev/Desktop/aurelio1.wav");
f.gain.val_(6);

o = MGU_onsetDetector(1);
o.thresh.val = -10;
o.odftype.val = \rcomplex;
f.connectToModule(o);
o.mingap.val = 20;
o.medianspan.val = 20;
o.relaxtime.val = 2;

)

f.sendSynth
o.sendSynth;

o.out.scope;

o.thresh.val = -5;


(

s.boot;

n = NetAddr("127.0.0.1", 8888);
c = OSCFunc({ |msg| msg.postln; n.sendMsg("/onset", msg[3])}, '/onset');

s.waitForBoot({

i = MGU_inModule(0, s);
i.inbus.val = 0;
o = MGU_onsetDetector(0, s);

o.thresh.val = -5;
o.odftype.val = \rcomplex;

i.connectToModule(o);
o.mingap.val = 20;
o.medianspan.val = 20;
o.relaxtime.val = 2;

fork({
	1.wait;
	i.sendSynth;
	o.sendSynth;
});

});

)




