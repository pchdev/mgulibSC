MGU_AbstractBufferModule : MGU_AbstractModule {

	var <buffer, <num_frames, <samplerate;

	readFileFunc {|path| // private
		var sound_file;
		sound_file = SoundFile.openRead(path);
		num_frames = sound_file.numFrames;
		this.num_outputs_(sound_file.numChannels);
		samplerate = sound_file.sampleRate;

		buffer ?? { buffer = Buffer.read(server, path, action: { this.bufferLoaded() })};
		("[] File:" + path + "succesfully loaded.").postln;

	}

	readFile { |filePath = nil|
		var soundFile, readFunc;
		filePath ?? { Dialog.openPanel({|path| this.readFileFunc(path)})};
		filePath !? { this.readFileFunc(filePath) };
	}

	// for granular synthesis & mts buffer-based generators

	readFileSCFunc { |path|
		var sound_file;
		buffer = [];
		sound_file = SoundFile.openRead(path);
		num_frames = sound_file.numFrames();
		this.num_outputs = sound_file.numChannels();
		samplerate = sound_file.sampleRate();

		sound_file.numChannels().do({|i|
			buffer = buffer.add(Buffer.readChannel(server, path, 0, -1, i, action: {
				if(i ==  (sound_file.numChannels() - 1)) { this.bufferLoaded() }}));
		});

		("[] File:" + path + "succesfully loaded.").postln;
	}

	readFileSeparatedChannels { |file_path = nil|
		file_path ?? { Dialog.openPanel({|path| this.readFileSCFunc(path)})};
		file_path !? { this.readFileSCFunc(file_path)};
	}

}