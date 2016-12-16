MGU_Module {

	classvar s_instance_count;
	var m_out, m_num_inputs, m_num_outputs, m_name;
	var m_instance_id;
	var m_group, m_internal_bus;
	var m_main_container, m_master_container, m_sends_container;
	var m_main_synthdef, m_master_synthdef, m_sends_synthdef;
	var am_synths, m_master_synth, am_send_synths;
	var p_level;
	var m_gui, m_description;
	var r_server;

	*new { |out = 0, num_inputs, num_outputs, name|
		^this.newCopyArgs(
			out,
			num_inputs,
			num_outputs,
			name)
	}

	moduleCtor {

		// count
		s_instance_count !? { s_instance_count = s_instance_count + 1 };
		s_instance_count ?? { s_instance_count = 1 };
		m_instance_id = s_instance_count;

		// defaults
		r_server = MGU_PREFS.getSelectedServer();
		m_name ?? {
			var classname = this.class.asCompileString;
			if(classname.split($_).size() == 2) {
				m_name = classname.split($_)[1] ++ "_" ++ m_instance_id } {
				m_name = classname;
			}
		};

		am_synths = [];
		am_send_synths = [];
		m_group = Group(1, 'addToTail');
		m_internal_bus = Bus.audio(r_server, m_num_outputs);

		m_main_container = MGU_container(m_name, nil, m_group, 4477);
		m_master_container = MGU_container("master", m_main_container, m_group, 4477);
		m_sends_container = MGU_container("sends", m_main_container, m_group, 4477);
		p_level = MGU_parameter(m_master_container, \level, Float,
			[-96, 12], 0, true, \dB, \amp);
	}

	// MINUIT
	registerToMinuit {|minuitInterface|
		minuitInterface.addContainer(m_main_container);
		m_main_container.parentContainer = minuitInterface;
	}

	generateUI {|always_on_top = false|}



}
	