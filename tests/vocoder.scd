(
{
	var in, saw, process, mix;
	in = SoundIn.ar(0);
	saw = LFPulse.ar(250);
	process = Array.newClear(20);
	20.do({|i|
		process.put(i, BBandPass.ar(in, 250 + (300 * i+1), 0.05) * BBandPass.ar(saw, 250 + (300 * i+1), 0.05))
	});
	mix = MulAdd(Mix.ar(process), 200);

	Out.ar(0, Pan2.ar(mix, 0))
}.play;

)