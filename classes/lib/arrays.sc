MGU_arrayLib {

	*pickHigherNote { |scale, note|
		var res;

		scale = scale.copy(); // copy, otherwise .removeAt() removes the notes from the ref

		("current note is" + note).postln;
		if(note.isNil) // if no note, randomly pick a note from the array
		{ res = [scale.choose()]}
		{ res = MGU_arrayLib.split(scale, note)[1] }; // otherwise res is the higher split

		if(res[0].isNil)
		{ scale.removeAt(scale.indexOf(note));
			scale.postln;
			res = scale.choose}
		{ res = res.choose() };

		^res;
	}

	*pickLowerNote { |scale, note|
		var res;
		scale = scale.copy();

		("current note is" + note).postln;

		if(note.isNil) // if no note, randomly pick a note from the array
		{ res = [scale.choose()]}
		{ res = MGU_arrayLib.split(scale, note)[0] }; // otherwise res is the higher split

		if(res[0].isNil)
		{ scale.removeAt(scale.indexOf(note));
			scale.postln;
			res = scale.choose}
		{ res = res.choose() };

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
