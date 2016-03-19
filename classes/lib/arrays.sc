MGU_arrayLib {

	*pickHigherNote { |scale, note_array|
		var res, note;
		note = note_array[note_array.maxIndex];
		note ?? { res = scale.choose };
		note !? { res = MGU_arrayLib.split(scale, note)[1].choose};
		^res;
	}

	*pickLowerNote { |scale, note_array|
		var res, note;
		note = note_array[note_array.minIndex];
		note ?? { res = scale.choose };
		note !? { res = MGU_arrayLib.split(scale, note)[0].choose};
		^res;
	}

	*split { |scale, note|

		var lower_split = [], higher_split = [], res;

		scale.size.do({|i|
			case
			{ scale[i] > note } { higher_split = higher_split.add(scale[i])}
			{ scale[i] < note } { lower_split = lower_split.add(scale[i])};
		});

		res = [lower_split, higher_split];
		^res;

	}

}
