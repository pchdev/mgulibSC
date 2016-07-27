MGU_conversionLib {

	*st_ratio { |semitones|
		var res = 2.pow(semitones/12);
		^res
	}

	*ratio_st { |ratio|
		var res = ratio.log2() * 12;
		^res
	}


}
	