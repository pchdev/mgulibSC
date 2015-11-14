MGU_wavetable {

	classvar <instanceCount;

	// arg defined attr.
	var server, numFrames, numPartials;

	// debug
	var <bufferArray;

	// private
	var <thisInstance;
	var <numBuffers;

	var <mainBuf;

	*new { |server, numFrames = 16384, numPartials = 64|
		^this.newCopyArgs(server, numFrames, numPartials).init
	}

	init {

		instanceCount !? { instanceCount = instanceCount + 1 };
		instanceCount ?? { instanceCount = 0};
		thisInstance = instanceCount;
		numBuffers = thisInstance * 100;
		server ?? { server = Server.default };
		bufferArray = [];

		mainBuf = Buffer.alloc(server, numFrames, 1, bufnum: numBuffers);
		bufferArray = bufferArray.growClear(1);
		bufferArray.put(bufferArray.size -1, numBuffers);
		numBuffers = numBuffers + 1;

		// build default sine table
		this.replaceWith(\sine);

	}

	// PUBLIC MTHODS

	replaceWith { |waveform|
		switch(waveform,
			\sine, { mainBuf.sine1(this.buildSine) },
			\triangle, { mainBuf.sine1(this.buildTriangle) },
			\square, { mainBuf.sine1(this.buildSquare) },
			\saw, { mainBuf.sine1(this.buildSaw) },
			\sawtooth, { mainBuf.sine1(this.buildSaw) });
	}

	addNext { |waveform| // FOR MORPHING -> LIBERER LES BUFFERS PRECEDENTS, REALLOC LE NUMBUFFERS
		var buffer;
		buffer = Buffer.alloc(server, numFrames, bufnum: numBuffers);
		bufferArray = bufferArray.growClear(1);
		bufferArray.put(bufferArray.size -1, numBuffers);
		switch(waveform,
			\sine, { buffer.sine1(this.buildSine) },
			\triangle, { buffer.sine1(this.buildTriangle) },
			\square, { buffer.sine1(this.buildSquare) },
			\saw, { buffer.sine1(this.buildSaw) },
			\sawtooth, { buffer.sine1(this.buildSaw) });
		numBuffers = numBuffers + 1;
		^buffer.bufnum
	}

	// PRIVATE MTHODS

	buildSine {
		var sineArray;
		sineArray = Array.fill(numPartials, { 0 });
		sineArray = sineArray.put(0, 1);
		^sineArray;

	}

	buildTriangle {
		var triArray;
		triArray = Array.fill(numPartials, {|i|
			var j = i + 1;
			var partial;
			var other_partial = j % 4;
			if(j % 2 == 0,
				{ partial = 0 },
				{ partial = j.squared.reciprocal });
			if(other_partial == 3,
				{ partial = partial * -1 });
			partial });
		^triArray;
	}

	buildSquare {
		var squareArray;
		squareArray = Array.fill(numPartials, {|i|
			var j = i + 1;
			var partial;
			if (j % 2 == 0,
				{ partial = 0 },
				{ partial = j.reciprocal });
			partial });
		^squareArray
	}

	buildSaw {
		var sawArray;
		sawArray = Array.fill(numPartials, {|i|
			(i + 1).reciprocal});
		^sawArray;
	}

}