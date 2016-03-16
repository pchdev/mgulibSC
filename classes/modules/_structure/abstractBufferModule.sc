MGU_AbstractBufferModule : MGU_AbstractModule {

	var <buffer, <numFrames, <sampleRate;

	readFileFunc {|path, read| // private
		var soundFile;
		soundFile = SoundFile.openRead(path);
		numFrames = soundFile.numFrames;
		this.numOutputs_(soundFile.numChannels);
		sampleRate = soundFile.sampleRate;

		buffer ?? { buffer = Buffer.read(server, path, action: { this.bufferLoaded() })};
		("[] File:" + path + "succesfully loaded.").postln;

	}

	readFile { |filePath = nil, read = false|
		var soundFile, readFunc;
		filePath ?? { Dialog.openPanel({|path| this.readFileFunc(path)})};
		filePath !? { this.readFileFunc(filePath, read) };
	}

}