p = MGU_pushInterface("127.0.0.1", 8888);

p.push_responder.setColor(11, 15, 15, 15);

MIDIIn.connectAll;
MIDIClient.init;
MIDIClient.sources;

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

o = OSCFunc.trace(true);

o = OSCFunc({|msg| msg.postln; f.value(msg[1])}, "/track/1/vu/L", nil, 8889);



