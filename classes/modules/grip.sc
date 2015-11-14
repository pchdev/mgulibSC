PO_grip : MGU_AbstractModule { // buffer freeze -- TBC

	*new {|out, server, numChannels, name|
		^super.newCopyArgs(out, server, numChannels, name).init.initParameters
	}

	initParameters {

	}

}