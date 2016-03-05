MGU_container {

	classvar instanceCount;

	var <>name, <>parentContainer, <>node, <>oscPort, module;
	var <>address;

	var <paramAccesses, <paramAddresses;
	var <contAccesses, <contAddresses;
	var <oscPort;
	var moduleGUI;

	*new { |address, parentContainer, node, oscPort, module|
		^this.newCopyArgs(address, parentContainer, node, oscPort, module).init
	}

	init {

		// global
		instanceCount !? { instanceCount = instanceCount + 1 };
		instanceCount ?? { instanceCount = 1 };

		if(name.beginsWith("/"), { name = name.drop(1) }); // name != address
		address = "/" ++ name;

		// init arrays
		paramAccesses = [];
		paramAddresses = [];
		contAccesses = [];
		contAddresses = [];

		parentContainer !? { parentContainer.registerContainer(this) }; // register to parent

	}

	directHierarchy { |type|
		var branchNb, children = [];
		branchNb = address.split($/).size;

		contAddresses.size.do({|i|
			if(contAccesses[i].address.split($/).size == (branchNb + 1), {
				contAddresses[i].postln;
				children = children.add(contAccesses[i].name.postln;)})});

		paramAddresses.size.do({|i|
				if(paramAccesses[i].address.split($/).size == (branchNb + 1), {
					children = children.add(paramAccesses[i].name.postln)})});
		^children
	}

	registerParameter { |parameter| // accessed by parameters

		parameter.defName = name ++ "_" ++ parameter.name;
		parameter.address = address ++ "/" ++ parameter.name;
		parameter.oscPort = oscPort;
		parameter.defaultNode = node;

		this.addParameter(parameter);
		parentContainer !? { parentContainer.addParameter(parameter) };
	}

	addParameter { |parameter|
		paramAccesses = paramAccesses.add(parameter);
		paramAddresses = paramAddresses.add(parameter.address);
	}

	registerContainer { |container| // accessed by children containers

		// setting
		container.address = address ++ container.address;
		container.oscPort = oscPort;
		container.node = node;
		container.paramAccesses.size.do({|i|
			var target = container.paramAccesses[i];
			target.address = address ++ target.address;
		});

		// merging accesses & addresses
		this.addContainer(container);
		parentContainer !? { parentContainer.addContainer(container) };
	}

	addContainer { |container|
		contAccesses = contAccesses.add(container);
		contAccesses = contAccesses ++ container.contAccesses;
		contAddresses = contAddresses.add(container.address);
		contAddresses = contAddresses ++ container.contAddresses;
		paramAccesses = paramAccesses ++ container.paramAccesses;
		paramAddresses = paramAddresses ++ container.paramAddresses;
	}

	makeSynthArray { |iter| // for generating Synths
		var synthArray = [];
		paramAccesses.size.do({|i|
			synthArray = synthArray.add(paramAccesses[i].defName);

			// Warning: if value is array, report to |iter| argument within Synth send iteration

			if(paramAccesses[i].val.isArray, {
				synthArray = synthArray.add(paramAccesses[i].val[iter])}, { // else
					synthArray = synthArray.add(paramAccesses[i].val)});
		});

		^synthArray

	}

	// CONTROL

	generateUI { |alwaysOnTop = false|
		moduleGUI = MGU_moduleGUI(address, paramAccesses, alwaysOnTop);
	}

	controlWithPush {

	}

}

