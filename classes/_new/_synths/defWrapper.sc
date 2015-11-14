MGU_defWrapper { // wraps contents into a SynthDef


	// user defined attr.
	var parent, rate;

	// returns
	var <def;

	// elements
	var <varDecl, <varDef, <process, <out;

	// accesses
	var <carrier, <rmod, <pmod;

	*new { |parent, rate = \audio|
		^this.newCopyArgs(parent, rate).init
	}

	init {

		def = "SynthDef(\\" ++ parent.name ++ ", {\n";
		varDecl = "var" + parent.name ++ "_process;\n";
		varDef = "";
		process = "";
		process = process ++ parent.name ++ "_process =";

	}

	refresh {
		this.init;
		this.addCarrier(carrier);
		if(parent.hasRM, { this.addRM(rmod) });
		this.wrapDef;
	}

	addCarrier { |gen|
		carrier = gen;
		varDecl = varDecl ++ carrier.genDef.varDecl;
		varDef = varDef ++ carrier.genDef.varDef;
	}

	addRM { |gen|
		rmod = gen;
		varDecl = varDecl ++ rmod.genDef.varDecl;
		varDef = varDef ++ rmod.genDef.varDef;
	}

	addPM {
	}

	wrapDef {

		// carrier only
		if((parent.hasRM == false) && (parent.hasPM == false), {
			process = process + carrier.name ++ "_oscil;\n";
		});

		// rmod
		if((parent.hasRM) && (parent.hasPM == false), {
			process = process + "Mix.ar(["
			++ carrier.name ++ "_oscil" + "*" + "(1 -" + parent.bal.defNameKr ++ "),"
			+ carrier.name ++ "_oscil" + "*" + rmod.name ++ "_oscil"
			+ "*" + parent.bal.defNameKr ++ "]);\n"});

		// pmod (tbi)

		// out
		out = "Out";
		if(rate == \audio, { out = out ++ ".ar" });
		if(rate == \control, { out = out ++ ".kr"});
		out = out ++ "(" ++ parent.outBus.defNameKr ++ ","
		+ "Pan2.ar(" ++ parent.name ++ "_process" ++ ","
		+ parent.pan.defNameKr
		++ "));\n";

		// wrapping def
		def = def ++ varDecl ++ varDef ++ process ++ out;
		def = def ++ "}).add";

		// sending SynthDef to server
		def.interpret;

	}


}