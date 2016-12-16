MGU_Node {

	classvar s_instance_count;
	var m_parent, m_name, m_node_group;
	var m_address;

	nodeCtor {

		s_instance_count !? { s_instance_count = s_instance_count + 1 };
		s_instance_count ?? { s_instance_count = 1 };

		// address & name processing ( name != address )
		if(m_name.beginsWith("/")) {m_name = m_name.drop(1)};
		m_address = "/" ++ m_name;

	}

	getAddress { ^m_address }
	getDefinitionName { ^m_name }
	setDefinitionName { |name| m_name = name }
	setAddress { |address| m_address = address }
	setNodeGroup { |node_group| m_node_group = node_group }

}

MGU_minuitDevice : MGU_Node {

	var m_name, m_osc_host_address, m_osc_send_port, m_osc_receive_port;
	var m_osc_osc_replier;
	var am_containers, am_parameters;

	*new {|device_name, device_address, device_send_port, device_receive_port|
		^this.newCopyArgs(
			device_name,
			device_address,
			device_send_port,
			device_receive_port).nodeCtor.minuitCtor()
	}

	minuitCtor {

	}

	registerNode { |node|
		node.setDefinitionName(m_name ++ "_"
			++ node.getDefinitionName());
		node.setAddress(m_address ++ "_"
			++ node.getAddress());
		node.setNodeGroup(m_node_group);

		if(node.isKindOf(MGU_parameter)) {
			am_parameters = am_parameters.add(node)} {
			am_containers = am_containers.add(node) };

	}

}

MGU_container2 : MGU_Node {

	var am_parameters, am_containers;

	*new {|parent, name, node_group|
		^this.newCopyArgs(
			parent,
			name,
			node_group).nodeCtor().containerCtor()
	}

	containerCtor {
		am_parameters = []; am_containers = [];
		m_parent !? { m_parent.registerNode(this) };
	}

	getChildrenParameters { ^am_parameters }
	getChildrenContainers { ^am_containers }

	registerNode { |node|
		node.setDefinitionName(m_name ++ "_"
			++ node.getDefinitionName());
		node.setAddress(m_address ++ "_"
			++ node.getAddress());
		node.setNodeGroup(m_node_group);

		if(node.isKindOf(MGU_parameter)) {
			am_parameters = am_parameters.add(node)} {
			am_containers = am_containers.add(node) };

	}

	makeSynthArray { |iter|
		var synth_array = [];
		am_parameters.do({|parameter|
			synth_array = synth_array.add(
				parameter.getDefinitionName())});
		^synth_array;
	}

}

MGU_parameter2 : MGU_Node {

	var m_type, am_range, m_default_value;
	var m_update_on_serv, m_in_unit, m_out_unit;
	var m_value, m_absolute_value;
	var m_custom_func_target, m_custom_func;
	var r_osc_replier, m_osc_func;
	var m_listening;
	var m_gui, m_description;
	var m_audio_bus, m_control_bus;

	*new { |container, name, node_group, type, range,
		default_value, update_on_serv,
		in_unit, out_unit|
		^this.newCopyArgs(
			container,
			name,
			node_group,
			type,
			range,
			default_value,
			update_on_serv,
			in_unit,
			out_unit).nodeCtor().parameterCtor()
	}

	parameterCtor {
		m_audio_bus = inf;
		m_control_bus = inf;
		m_listening = false;
		m_description = "no description available";
		m_parent !? { m_parent.registerNode(this) };
		m_value = m_default_value;
		this.initOSC();
	}

	initOSC {
		m_osc_func !?  { m_osc_func.free() };
		m_osc_func = OSCFunc({|msg, time, addr, recv_port|
			msg.postln();
			setValue(msg[1])}, m_address, nil, MGU_PREFS.getOSCReceivePort());
	}

	enableListening { m_listening = true }
	disableListening { m_listening = false }

	getValue { |in_value = false|
		if(in_value) { ^m_absolute_value } { ^m_value }
	}

	setValue { |value|

		if((value == inf) || (value == -inf)) {
			value = value.asInteger()};
		if((value.isKindOf(Integer)) && (m_type == Float)) {
			value = value.asFloat()};
		if((value.isKindOf(Float)) && (m_type == Integer)) {
			value = value.asInteger()};
		if(value.isKindOf(m_type) == false) {
			Error("MGU_parameter: wrong type for value").throw() };

		m_absolute_value = value;

		if((m_in_unit.notNil()) && (m_out_unit.notNil())) {
			MGU_LIB.translateValue(Ref(value), m_in_unit, m_out_unit)};

		if(m_update_on_serv) {
			m_node_group.set(m_name, m_value);
		};

		if((m_custom_func_target.notNil()) && (m_custom_func.notNil())) {
			(m_custom_func_target ++ "." ++ m_custom_func).interpret();
		};

		if(m_listening) {MGU_PREFS.sendMinuitReply(m_address, m_value)};

	}

	// CONVENIENCE DEF MTHODS (change to kr, ar etc.)

	smb {^m_name.asSymbol}
	kr {^m_name.asSymbol.kr}
	ar {^m_name.asSymbol.ar}
	tr {^m_name.asSymbol.tr}
	defNameKr {^("\\" ++ m_name ++ ".kr")}
	defNameAr {^("\\" ++ m_name ++ ".ar")}
	defNameTr {^("\\" ++ m_name ++ ".tr")}

}
