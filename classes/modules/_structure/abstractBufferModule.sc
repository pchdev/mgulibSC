MGU_AbstractBufferModule : MGU_AbstractModule {

	var <buffer, <numFrames, <sampleRate;

	readFileFunc {|path, read| // private
		var soundFile;
		soundFile = SoundFile.openRead(path);
		numFrames = soundFile.numFrames;
		numOutputs = soundFile.numChannels;
		sampleRate = soundFile.sampleRate;
		buffer !? {
			var temp;
			fork ({ // crossfade process
				"crossfading".postln;
				temp = buffer;
				this.killSynths;
				buffer = Buffer.read(server, path);
				this.sendSynth;
				2.wait;
				temp.free;
			})
		};

		buffer ?? { buffer = Buffer.read(server, path, action: { this.bufferLoaded() })};

		("[] File:" + path + "succesfully loaded.").postln;
		//out ?? { out = Bus.audio(server, numChannels) };
		//read !? { this.sendSynth };
	}

	readFile { |filePath = nil, read = false|
		var soundFile, readFunc;
		filePath ?? { Dialog.openPanel({|path| this.readFileFunc(path)})};
		filePath !? { this.readFileFunc(filePath, read) };
	}

}