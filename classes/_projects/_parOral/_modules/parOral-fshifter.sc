PO_fShifter : MGU_AbstractModule { // frequency shifter module -- TBC

	var freq;
	var negpos;

	*new { |out, server, numChannels = 1, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters
	}

	initParameters {

		freq = MGU_parameter(container, \freq, Float, [-20000, 20000], 50, true);
		inbus = 0;


	}

}

	