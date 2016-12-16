MGU_PREFS {

	classvar s_osc_receive_port;
	classvar s_osc_send_port;
	classvar s_osc_replier;
	classvar s_minuit_interface;

	*getOSCReceivePort { ^s_osc_receive_port }
	*getOSCSendPort { ^s_osc_send_port }
	*getOSCReplier { ^s_osc_replier }
	*getMinuitInterface { ^s_minuit_interface }

	*initMinuit { |device_name, osc_send_port = 13579, osc_receive_port = 4477|
		s_minuit_interface = MGU_minuitInterface(device_name, osc_receive_port, true);
		s_osc_replier = NetAddr("127.0.0.1", osc_receive_port);
	}

	*sendMinuitReply { |address, value|
		s_osc_replier.sendBundle(nil);
	}

	*registerNodeToMinuit { |node|
		s_minuit_interface.registerNode(node);
	}
}