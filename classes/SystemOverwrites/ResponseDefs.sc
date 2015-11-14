+ OSCFunc {

	*cmdPeriod { this.trace(false) }

	init {|argfunc, argpath, argsrcID, argrecvPort, argtemplate, argdisp|
		path = (argpath ? path).asString;
		//if(path[0] != $/, {path = "/" ++ path}); // demand OSC compliant paths
		path = path.asSymbol;
		srcID = argsrcID ? srcID;
		recvPort = argrecvPort ? recvPort;
		if(recvPort.notNil and: {thisProcess.openUDPPort(recvPort).not}, {
			Error("Could not open UDP port"+recvPort).throw;
		});
		argtemplate = argtemplate.collect({|oscArg|
			if(oscArg.isKindOf(String), {oscArg.asSymbol}, {oscArg}); // match Symbols not Strings
		});
		argTemplate = argtemplate ? argTemplate;
		func = argfunc ? func;
		dispatcher = argdisp ? dispatcher;
		this.enable;
		allFuncProxies.add(this);
	}


}
