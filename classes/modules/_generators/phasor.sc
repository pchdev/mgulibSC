MGU_phasor {

	*ar { |freq = 1, phase = 0|
		var count = 0, countminusone = 0;
		count = count + countminusone;
		countminusone = Delay1.ar(count);
		count.poll;
		^count;
	}

}

