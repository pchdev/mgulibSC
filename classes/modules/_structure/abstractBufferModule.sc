MGU_AbstractBufferModule : MGU_AbstractModule {

	var <buffer, <num_frames, <samplerate;

	readFileFunc {|path, read| // private
		var soundFile;
		soundFile = SoundFile.openRead(path);
		num_frames = soundFile.numFrames;
		this.num_outputs_(soundFile.numChannels);
		samplerate = soundFile.sampleRate;

		buffer ?? { buffer = Buffer.read(server, path, action: { this.bufferLoaded() })};
		("[] File:" + path + "succesfully loaded.").postln;

	}

	readFile { |filePath = nil, read = false|
		var soundFile, readFunc;
		filePath ?? { Dialog.openPanel({|path| this.readFileFunc(path)})};
		filePath !? { this.readFileFunc(filePath, read) };
	}

}