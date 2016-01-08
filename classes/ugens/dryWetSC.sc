SCdrywet {

	*ar { |in_dry, in_wet, mix = 0.5|
		var process1, process2, process3;
		process1 = in_dry * (1-mix);
		process2 = in_wet * mix;
		process3 = process1 + process2;
		^process3;
	}
}