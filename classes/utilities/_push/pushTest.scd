p = MGU_pushInterface("127.0.0.1", 8888);

p.push_responder.setPadColor(100, 10, 5, 2);
p.push_responder.setControl(27, 6);

MIDIIn.connectAll;
MIDIClient.init;
MIDIClient.sources;

MIDIFunc.trace(true)
MIDIFunc.trace(false)

o = OSCFunc.trace(true);
o = OSCFunc({|msg| msg.postln; f.value(msg[1])}, "/track/1/vu/L", nil, 8889);





