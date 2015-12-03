MGU_minuitInterface {

	var address, port;
	var <paramAccesses, <paramAddresses;
	var <contAccesses, <contAddresses;
	var oscDiscover, oscGet, oscListen, <respAddr;

	*new { |address = "superColl", port = 3127|
		^this.newCopyArgs(address, port).init
	}

	init {

		respAddr = NetAddr("127.0.0.1", 13579);

		// init arrays
		paramAccesses = [];
		paramAddresses = [];
		contAccesses = [];
		contAddresses = [];

		// init OSC responders
		oscDiscover = OSCFunc({|msg, time, addr, recvPort| msg.postln;
			this.parseDiscovery(msg[1])}, "i-score?namespace", nil, port);

		oscGet = OSCFunc({|msg, time, addr, recvPort| msg.postln;
			this.parseGet(msg[1])}, "i-score?get", nil, port);

		oscListen = OSCFunc({|msg, time, addr, recvPort| msg.postln;
			this.parseListen(msg[1], msg[2])}, "i-score?listen", nil, port);

	}

	addParameter { |parameter|
		paramAccesses = paramAccesses.add(parameter);
		paramAddresses = paramAddresses.add(parameter.address);
	}

	addContainer { |container|
		container.oscPort = port;
		contAccesses = contAccesses.add(container);
		contAccesses = contAccesses ++ container.contAccesses;
		contAddresses = contAddresses.add(container.address);
		contAddresses = contAddresses ++ container.contAddresses;
		paramAccesses = paramAccesses ++ container.paramAccesses;
		paramAddresses = paramAddresses ++ container.paramAddresses;
	}

	// DISCOVERY

	parseDiscovery { |discoveryMsg|
		if(discoveryMsg == '/', { this.queryTreeRoot }, {
			this.queryNode(discoveryMsg)});
	}

	queryTreeRoot {
		var applNodes;
		applNodes = this.applicationNodes;
		this.sendResponse("namespace", '/', \Application, applNodes, attributes:
			[\debug, \version, \type, \name, \author]);
	}

	applicationNodes {
		var nodes = [];
		contAddresses.size.do({|i|
			if(contAddresses[i].split($/).size == 2, {
				nodes = nodes.add(contAddresses[i].drop(1).asSymbol)})
		});

		^nodes;
	}

	queryNode { |node|
		var nodeAccess, nodeType, subNodes = [], attributes = [];
		node = node.asString;
		contAccesses.size.do({|i| // node is container ?
			if(node == contAccesses[i].address, {
				nodeAccess = contAccesses[i]});
		});

		nodeAccess ?? {
			paramAccesses.size.do({|i| // then node is parameter
				if(node == paramAccesses[i].address, {
					nodeAccess = paramAccesses[i]});
			});
		};

		nodeAccess ?? { Error("Minuit: error, couldn't find node:" + node).throw };

		if(nodeAccess.isKindOf(MGU_container), {
			nodeType = \Container;
			subNodes = nodeAccess.directHierarchy();
			attributes = [\tag, \service, \description, \priority];
		}, { // else
				nodeType = \Data;
				attributes = [\rangeBounds, \service, \active, \tag,
				\type, \repetitionsFilter, \description, \priority, \valueDefault, \value]
		});

		this.sendResponse("namespace", node, nodeType, subNodes, attributes: attributes);
	}

	// GET

	parseGet { |getMsg|

		var node, attribute;

		getMsg = getMsg.asString.split($:);
		node = getMsg[0];
		attribute = getMsg[1];

		this.getQuery(node, attribute);

	}

	getQuery { |node, attribute|

		var nodeAccess, nodeType, response, attributeVal;

		contAddresses.size.do({|i|
			if(node == contAddresses[i], { nodeAccess = contAccesses[i] })});
		nodeAccess ?? {
			paramAddresses.size.do({|i|
				if(node == paramAccesses[i].address, {
					nodeAccess = paramAccesses[i]});
			});
		};

		nodeAccess ?? { Error("Minuit: error, couldn't find node:" + node).throw };

		switch(attribute,
			nil, { },
			"rangeBounds", { attributeVal = nodeAccess.range },
			"service", { if(nodeAccess.isKindOf(MGU_container),
				{ attributeVal = ["model"]}, { attributeVal = ["parameter"] })},
			"value", { attributeVal = [nodeAccess.val]},
			"priority", { attributeVal = [0] },
			"type", { switch(nodeAccess.type,
				Float, { attributeVal = [\decimal] },
				Integer, { attributeVal = [\integer]},
				String, { attributeVal = [\string]},
				Symbol, { attributeVal = [\string]}
			)},
			"rangeClipmode", { attributeVal = [\both] }
		);

		attribute ?? { response = node + attributeVal };
		attribute !? { response = node ++ ":" ++ attribute };
		this.sendResponse("get", response, values: attributeVal);
	}

	// LISTEN

	parseListen { |listenMsg1, listenMsg2|
		var node, state;
		listenMsg1 = listenMsg1.asString.split($:);
		node = listenMsg1[0];
		state = listenMsg2.asString;
		this.listenQuery(node, state);

	}

	listenQuery { |node, state|

		var nodeAccess, response;
		response = node ++ ":value";

		paramAddresses.size.do({|i|
				if(node == paramAccesses[i].address, {
					nodeAccess = paramAccesses[i]});
		});

		// if state = enable, access node to send value whenever it changes
		if(state == "enable", { nodeAccess.enableListening(respAddr, address) }, {
			nodeAccess.disableListening});

		this.sendResponse("listen", response, values: [nodeAccess.val]);
	}

	sendResponse { |msgType, subjectNode, nodeType, nodes, values, attributes|

		var msgArray = [];

		msgArray = msgArray.add(address.asString ++ ":" ++ msgType.asString);
		msgArray = msgArray.add(subjectNode.asSymbol);

		if(msgType == "namespace", {
			msgArray = msgArray.add(nodeType.asSymbol)});

		if((nodeType == \Container) || (nodeType == \Application), {
			msgArray = msgArray.add('nodes={');
			msgArray = msgArray ++ nodes;
			msgArray = msgArray.add('}')});

		if(msgType == "namespace", { msgArray = msgArray.add('attributes={');
			msgArray = msgArray ++ attributes;
			msgArray = msgArray.add('}')});

		if(msgType == "get", {
			values.size.do({|i|
				msgArray = msgArray.add(values[i])})});

		if(msgType == "listen", {
			values.size.do({|i|
				msgArray = msgArray.add(values[i])})});


		msgArray.postln;

		respAddr.sendBundle(nil, msgArray);
	}

}