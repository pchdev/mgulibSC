MIDIIn.connectAll;

// MIDI RETRACE OF FUNCTIONS EVALUATED;

a = [];
b = [];

MIDIIn.sysex = {|uid, data|
	switch(data.at(4),
		6, { "MIDI REC received".postln; t = thisThread.seconds;
			this.codeDump = { |code| a = a.add(code); b = b.add(thisThread.seconds - t)}},
		1, { "MIDI STOP received".postln; y = thisThread.seconds;
		codeDump = nil});
}

f = { "jose.postln" };
f.value;
a;
b;

a.at(0);
a.at(1).interpret;

fork { // doesn't work...
	a.size.do({|i|
		a.at(i).interpret;
		b.at(i).interpret.wait;
})}

Routine({ // doesn't work either...
	a.at(1).interpret;
	b.at(1).interpret.wait;
}).play;


OSCFunc.trace(false);

MIDIFunc.cc({|val, num, chan, src| val.postln; num.postln});
MIDIFunc.noteOn({|val| val.postln});

p = MGU_pushInterface;
p.topCcArray;
p.oscip;

t = MGU_topCC;
t.address;



