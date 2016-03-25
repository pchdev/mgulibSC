JIM_processingCommunication {

	var <send_port, <receive_port, <>print, <netaddr;
	var <deviation_x, <deviation_y;
	var <mouse_x, <mouse_y, <mouse_click;
	var <flocksize;

	var <cohesion, <alignment, <avoidance;
	var midiout;


	*new{ |send_port, receive_port, print|
		^this.newCopyArgs(send_port, receive_port, print).init
	}

	init {

		var print_func = { |print, msg| if(print) { msg.postln }};

		flocksize = 0;

		netaddr = NetAddr("127.0.0.1", send_port);

		OSCFunc({|msg|
			print_func.value(print, msg);
			deviation_x = msg[1]}, '/flock/deviation/x', nil, receive_port);

		OSCFunc({|msg|
			print_func.value(print, msg);
			deviation_y = msg[1]}, '/flock/deviation/y', nil, receive_port);

		OSCFunc({|msg|
			print_func.value(print, msg);
			mouse_x = msg[1]}, '/mouse/x', nil, receive_port);

		OSCFunc({|msg|
			print_func.value(print, msg);
			mouse_x = msg[1]}, '/mouse/y', nil, receive_port);

		OSCFunc({|msg|
			print_func.value(print, msg);
			this.valueMouseClick(msg[1]);
			mouse_x = msg[1]}, '/mouse/click', nil, receive_port);

	}

	valueMouseClick { |value|
	}

	setCohesion { |val|
		val = val.asFloat;
		cohesion = val;
		netaddr.sendMsg("/flock/cohesion", val)
	}

	setAlignment { |val|
		val = val.asFloat;
		alignment = val;
		netaddr.sendMsg("/flock/alignment", val)
	}

	setAvoidance { |val|
		val = val.asFloat;
		avoidance = val;
		netaddr.sendMsg("/flock/avoidance", val)
	}

	clearFlock {
		netaddr.sendMsg("/flock/clear");
	}

	addBoid { |x = 10, y = 10|
		x = x.asFloat;
		y = y.asFloat;
		flocksize = flocksize + 1;
		netaddr.sendMsg("/flock/add", x, y);
	}






}




