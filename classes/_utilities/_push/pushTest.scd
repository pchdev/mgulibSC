p = MGU_pushInterface(nil, nil);

p.topCcArray[0].val

MIDIFunc.trace(true)
MIDIFunc.trace(false)

n = NetAddr("127.0.0.1", 8888);

n.sendMsg("/vkb_midi/0/note/56", 127);

n.sendMsg("/vkb_midi/0/note/43", 127);
n.sendMsg("/vkb_midi/0/note/43", 0);

n.sendMsg("/track/1/recarm", 1);
n.sendMsg("/track/1/recarm", 0);

n.sendMsg("/track/2/recarm", 1);
n.sendMsg("/track/2/recarm", 0);

n.sendMsg("/vkb_midi/1/note/50", 50);
n.sendMsg("/vkb_midi/1/note/50", 0);
n.sendMsg("/vkb_midi/1/note/62", 50);
n.sendMsg("/vkb_midi/1/note/62", 0);

n.sendMsg("/vkb_midi/1/pitch", 6400);

m = MIDIFunc.noteOn({|vel, note| [vel, note].postln});
o = MIDIFunc.noteOff({|vel, note| [vel, note].postln});

o = OSCFunc.trace(true);

o = OSCFunc({|msg| msg.postln; f.value(msg[1])}, "/track/1/vu/L", nil, 8889);





a = Array.fill(8, {|i| 1 + (8 * i)});

button[0].setColor(0, msg[1]

// if button 0 between 0 & 0.125 (or higher) -> green
// if signal higher than 0.125 -> max green

// if button 1 between 0.125 & 0.250 -> green
// if button 2 between 0.250 & 0.375 -> green
// if button 3 between 0.375 & 0.5 -> green
// if button 4 between 0.5 & 0.625 -> green
// if button 5 between 0.625 & 0.750 -> green with light yellow
// if button 6 between 0.750 & 0.875 -> yellow
// if button 7 between 0.875 & 1 -> orange
// if down toggle higher than 1 -> red

		0.100.linlin(0, 0.125, 0, 15).round(1);


