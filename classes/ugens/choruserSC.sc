ChoruserSC {

	*ar { |in, freq = 1.0, depth = 100, dtime = 0.02, fbk = 50|
		var numdelays, process, lfnoise, delay, out, fbkline = 0;

		numdelays = 8;
		in = in * numdelays.reciprocal;

		lfnoise = Array.fill(numdelays, {
			(LFNoise1.ar(freq) + 1) / 2});
		lfnoise = lfnoise * dtime;

		delay = DelayC.ar(in + fbkline, 0.1, lfnoise);
		fbkline = delay * (fbk / 100);
		process = Pan2.ar(delay, LFNoise1.ar(freq));
		process = process.sum;
		^process;
	}
}