MGU_moduleMaster {

	var master_internal, type, inbus, node_group, parent_container, osc_port;
	var <container;
	var <level, <mix, <pan;

	*new {|master_internal, type, inbus, node_group, parent_container, osc_port|
		^this.newCopyArgs(master_internal, type, inbus, node_group, parent_container, osc_port).init
	}

	init {

		container = MGU_container("master", parent_container, node_group, osc_port);
		level = MGU_parameter(container, \level, Float, [-96, 12], 0, true, \dB, \amp);




	}



}