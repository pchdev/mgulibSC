

n = MGU_synth2(0, s);
n.carrier.freq.val = 332;
n.carrier.freq.val;
n.addRM;

n.makeSynth(330);
n.makeSynth(378);
n.killSynth(0);

n.carrier.freq.val_({|val| val - 220}, true, n.lastVoice);
n.carrier.freq.val_(446, true, n.lastVoice);

n.lastVoice.get("carrier_freq".asSymbol, {|v| v.postln});

n.lastVoice.get(n.carrier.freq.defName.asSymbol, {|val| v = val});

n.carrier.freq.defName.isKindOf(Symbol);

n.synthArray.at(0).nodeID;

n.voice(0);
n.lastVoice;

n.carrier.freq.val_(61.midicps, true, n.voice(1));
n.carrier.amp.val_(0.5, true, n.voice(0));

(
8.do({|i|
	n.makeSynth(110 * (i+1));
});
)

n.killAll;

a = [0, 1, 2];
a.at(0) = 3;
a.put(0, 3);
a


