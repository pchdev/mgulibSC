MGU_parameter {

	classvar instanceCount;

	var <container, <name, <type, <range, <default, <>alwaysOnServ;
	var <>inUnit, <>outUnit, <>sr;
	var <>uiType;
	var <>defaultNode;
	var <val;
	var <>address, <>defName;
	var oscFunc, <>oscPort;
	var <>parentAccess;
	var listening, netaddr_responder, responder_device;


	*new { |container, name, type, range, default, alwaysOnServ = false,
		inUnit, outUnit, sr = 44100, uiType|
		^this.newCopyArgs(container, name, type, range, default, alwaysOnServ,
			inUnit, outUnit, sr, uiType).init
	}

	init {

		instanceCount !? { instanceCount = instanceCount + 1 };
		instanceCount ?? { instanceCount = 1 };

		listening = false;

		// register to parent container
		container.registerParameter(this);
		this.val_(default, onServ: false);

		// init OSC Function
		oscFunc = OSCFunc({|msg, time, addr, recvPort|
			msg.postln;
			this.val_(msg[1])}, address, nil, oscPort);

	}

	unitCheck { |value|

		switch(inUnit,

			\s, { switch(outUnit,
				\ms, { val = value * 1000 },
				\samps, { val = value * sr })},
			\ms, { switch(outUnit,
				\s, { val = value / 1000 },
				\samps, { val = (value / 1000) * sr})},
			\samps, { switch(outUnit,
				\s, { val = value / sr },
				\ms, { val = (value / sr) * 1000 })},
			\dB, {if(outUnit == \amp, { val = value.dbamp })},
			\amp, {if(outUnit == \dB, { val = value.ampdb })});

	}

	val_ { |value, node, interp = false, duration = 2000, curve = \lin, onServ,
		absolute_unit = false|

		var process;
		process = {
			value.size.do({|i|
				// casts & type tests
				if((value[i] == inf) || (value[i] == -inf), {
					value.put(i, value[i].asInteger)});

				if((value[i].isKindOf(Integer)) && (type == Float), {
					value.put(i, value[i].asFloat)});


				if((value[i].isKindOf(Float)) && (type == Integer), {
					value.put(i, value[i].asInteger)});

				if((value[i].isKindOf(type) == false) && (type != Array), {
					Error("[PARAMETER] /!\ WRONG TYPE:" + name).throw });

				// range check
				if((value[i].isKindOf(Integer)) || (value[i].isKindOf(Float)), {
					if(value[i] < range[0], { value.put(i, range[0]) });
					if(value[i] > range[1], { value.put(i, range[1]) });
				});

				if(type == Array, { val = value }, { val = value[i] });

				// unit conversion
				if((inUnit.notNil) && (outUnit.notNil) && (absolute_unit == false), {
					this.unitCheck(val)
				});

				// sending value on server
				if(onServ, {
					node[0] ?? { node = [defaultNode] };
					node[0] ?? { Error("[PARAMETER] /!\ NODE NOT DEFINED" + name).throw };
					node[i].set(defName, val);
				});

			});

			parentAccess !? { parentAccess.paramCallBack(name, value)};
			if(listening,
				{ netaddr_responder.sendBundle(nil, [responder_device ++ ":listen",
					address ++ ":value", val].postln)});

		};

		// STARTS HERE
		onServ ?? { if(alwaysOnServ, { onServ = true }, { onServ = false })};

		// node & value always as array
		if(node.isArray == false, { node = [node] });

		// if value is a function
		if(value.isFunction, {
			var func, currentVal = [];
			func = value;
			if(onServ, {
				node.size.do({|i|
					node[i].get(defName.asSymbol, {|kval|
						currentVal = currentVal.add(kval);
						value = func.value(currentVal);
						process.value})}, { // else -> not on server
						value = func.value(val);
						process.value});
			});
			}, { if(value.isArray == false, { value = [value] });  // else : not a function
			 	 process.value});

	}

	enableListening { |netaddr, device|
		listening = true;
		netaddr_responder ?? { netaddr_responder = netaddr };
		responder_device ?? { responder_device = device };

	}

	disableListening {
		listening = false;
	}


	// DEF MTHODS (change to kr, ar etc.)

	smb {
		^defName.asSymbol
	}

	smbKr {
		^defName.asSymbol.kr
	}

	smbAr {
		^defName.asSymbol.ar
	}

	smbTr {
		^defName.asSymbol.tr
	}

	defNameKr {
		^("\\" ++ defName ++ ".kr");
	}

	defNameAr {
		^("\\" ++ defName ++ ".ar");
	}

	defNameTr {
		^("\\" ++ defName ++ ".tr");
	}

}