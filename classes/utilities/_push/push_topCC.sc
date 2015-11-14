MGU_topCC {

	var <>address, ccNum, step, <val;
	var mul, add;
	var netaddr;

	*new { |address, ccNum = 71, step = 0.01, val = 0.0, mul = 1, add = 0, netaddr|
		^this.newCopyArgs(address, ccNum, step, val, mul, add, netaddr).init;
	}

	init {

		address ?? { address = "/track/" ++ (ccNum - 70) ++ "/volume"};
		MIDIFunc.cc({|v, num| this.val_(v)}, ccNum);
	}

	val_ { |value|

		if(value < 100, {
			val = val + step }, { // else decrease
				val = val - step });

		if(val <= 0.0, { val = 0.0 });
		if(val >= 1.0, { val = 1.0 });

		val = val * mul;
		val = val + add;

		netaddr !? { netaddr.sendBundle(nil, [address, val].postln) };

	}
}
	