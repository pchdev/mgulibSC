MGU_conversionLib {

	*st_ratio { |semitones|
		var res = 2.pow(semitones/12);
		^res
	}

	*ratio_st { |ratio|
		var res = 0;
		^res
	}

}
	