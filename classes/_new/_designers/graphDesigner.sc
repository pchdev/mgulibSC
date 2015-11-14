MGU_graphDesigner {

	var <>mode;
	var <envArray, <lfoArray;
	var <processArray;
	var graphFunc;

	*new { |mode = \effect, nbInputs = 1, nbOutputs = 1|
		^this.newCopyArgs(mode, nbInputs, nbOutputs).init
	}

	init {

		processArray = [];

	}

	addUGen { |name, uGenString|

		var uGenName, arguments;
		var argumentNames = [];
		var lineArray = [];
		arguments = [];

		// registering process name
		uGenString = uGenString.split($();
		uGenName = uGenString[0];
		lineArray = lineArray.add(name);

		// checking UGen validity
		if(uGenName.interpret.isUGen, {
			("[graphDesigner:" + uGenName + "succesfully added]").postln }, {
			Error("[graphDesigner:" + uGenName + "is not a valid UGen]").throw });

		// collecting arguments from string
		arguments = uGenString[1].split($))[0].split($,);

		// getting argument names
		arguments.size.do({|i|
			argumentNames = argumentNames.add(uGenName.interpret.argNameForInputAt(i))
		});

		// processing lineArray format;
		// #0 = process name, eg : osc1
		// #1 = ugen type, eg : SinOsc.ar
		// #2 = array of argument names, eg : [freq, phase, mul, add]
		// #3...n = parameter arrays containing :
		//          #0 : control name of parameter or static value
		//          #1 : envelope variable name (if an envelope has been added)
		//          #2 : lfo variable name (if an lfo control has been added)

		lineArray = lineArray.add(uGenName);
		lineArray = lineArray.add(argumentNames);
		arguments.size.do({|i|
			if(arguments[i].contains("map"), {
				switch(arguments[i].split($_)[1],
					"kr", {lineArray = lineArray.add(
						[name ++ "_" ++ argumentNames[i] ++ ".smbKr", nil, nil])},
					"ar", {lineArray = lineArray.add(
						[name ++ "_" ++ argumentNames[i] ++ ".smbAr", nil, nil])},
					"tr", {lineArray = lineArray.add(
						[name ++ "_" ++ argumentNames[i] ++ ".smbTr", nil, nil])});
			}, {
				lineArray = lineArray.add([arguments[i], nil, nil])})});

		processArray = processArray.add(lineArray);
		processArray.postln;

	} // PRIVATE

	formatUGen { |ugen, parameterArray|
		var res;

		res = ugen ++ "(";
		parameterArray.size.do({|i|
			res = res ++ parameterArray[i];
			if(i != (parameterArray.size - 1), {
				res = res ++ ", ";
			})
		});

		res = res ++ ");\n";

		^res;

	} // PRIVATE

	parameterLookup { |ugenNameSymbol, parameterName|
		var ugenIndex, parameterIndex, parameterNameList;

		// identifying ugen
		processArray.size.do({|i|
			if(processArray[i][0] == ugenNameSymbol, {
				ugenIndex = i});
		});

		ugenIndex ?? { Error("[graphDesigner] Error: couldn't find specified Unit Generator").throw };

		// identifying parameter
		parameterNameList = processArray[ugenIndex][2];
		parameterNameList.size.do({|i|
			if(parameterName == parameterNameList[i], {
				parameterIndex = i})});

		parameterIndex ?? { Error("[graphDesigner] Error: couldn't find specified parameter").throw };

		^[ugenIndex, parameterIndex];

	}


	addEnvOnParameter { |ugenNameSymbol, parameterName,
		gate = 1, levelScale = 1, levelBias = 0, timeScale = 1, doneAction = 0|

		var lookupResult, ugenIndex, parameterIndex;
		var envName;

		lookupResult = this.parameterLookup(ugenNameSymbol, parameterName);
		ugenIndex = lookupResult[0];
		parameterIndex = lookupResult[1];

		processArray[ugenIndex][parameterIndex+3][1] =
		[gate, levelScale, levelBias, timeScale, doneAction];

		envName = ugenNameSymbol ++ "_" ++ parameterName ++ "_" ++ "env";

		"[graphDesigner] envelope succesfully added".postln;

	}

	addLFOOnParameter { |ugenNameSymbol, parameterName, busnum|

		var lookupResult, ugenIndex, parameterIndex;
		var lfoName;

		lookupResult = this.parameterLookup(ugenNameSymbol, parameterName);
		ugenIndex = lookupResult[0];
		parameterIndex = lookupResult[1];

		processArray[ugenIndex][parameterIndex+3][2] = busnum;

		lfoName = ugenNameSymbol ++ "_" ++ parameterName ++ "_" ++ "lfo";

		"[graphDesigner] LFO succesfully added".postln;

	}

	compileGraphFunc {|processTarget, panner = false, pos = 0|

		var graphFunc = "{ ";

		// looking for envelopes

		processArray.size.do({|i|
			processArray[i][2].size.do({|j|
				processArray[i][j+3][1] !? { // if envelope slot not nil
					var envName = processArray[i][0] ++ "_" ++ processArray[i][2][j] ++ "_env";
					graphFunc = graphFunc ++ "var" + envName + "=" + "\\" ++ envName.asSymbol
					++ ".kr(Env.newClear(8).asArray);\n";
		}})});

		// looking for LFOs

		processArray.size.do({|i|
			processArray[i][2].size.do({|j|
				processArray[i][j+3][2] !? { // if lfo slot not nil
					var lfoName = processArray[i][0] ++ "_" ++ processArray[i][2][j] ++ "_lfo";
					graphFunc = graphFunc ++ "var" + lfoName + "=" + "In.kr(" ++ // add choice (ar,kr)
					processArray[i][j+3][2] ++ ");\n";

		}})});

		// decl. + def of UGens
		processArray.size.do({|i|
			var paramArray = [];
			graphFunc = graphFunc ++ "var" + processArray[i][0] + "=";
			processArray[i][2].size.do({|j|
				var res;
				res = processArray[i][j+3][0];
				processArray[i][j+3][1] !? {
					res = res + "* EnvGen.kr(" ++ processArray[i][0] ++ "_" ++
					processArray[i][2][j] ++ "_env," +
					processArray[i][j+3][1][0] ++ "," +
					processArray[i][j+3][1][1] ++ "," +
					processArray[i][j+3][1][2] ++ "," +
					processArray[i][j+3][1][3] ++ "," +
					processArray[i][j+3][1][4] ++ ")";
				};

				processArray[i][j+3][2] !? {
					res = res + "*" + processArray[i][0] ++ "_" ++ processArray[i][2][j]
					++ "_lfo"
				};
				paramArray = paramArray.add(res);
			});

			graphFunc = graphFunc + this.formatUGen(processArray[i][1], paramArray);

		});

		// process definition

		// Out

		graphFunc = graphFunc ++ "Out.ar(\\out.kr,";
		if(panner, { graphFunc = graphFunc + "Pan2.ar(" ++ processTarget ++ "," + pos ++")"}, {
			graphFunc = graphFunc + processTarget});
		graphFunc = graphFunc ++ ")";

		// ending

		graphFunc = graphFunc + "}";
		graphFunc.postln;
		^graphFunc



	}

}



	