JIM_falconInterface : MGU_AbstractModule {

	var <falcon_port, falcon_osc, falcon_midi;

	var <boid_density, <boid_grainsize, <boid_pan, <boid_note, <boid_gain;
	var <granpad_note, <granpad_gain;
	var <gransects_note, <gransects_gain;
	var <bassfm_note, <bassfm_gain;
	var <noise_note, <noise_gain;
	var <create_boid, <reset_score;
	var granpad_playing, bassfm_playing;
	var granpad_scale, bassfm_scale;


	*new { |out = 0, server, numInputs = 2, numOutputs = 2, name|
		^super.newCopyArgs(out, server, numInputs, numOutputs, name).type_(\effect)
		.init.initModule.initMasterDef
	}


	initModule {

		MIDIClient.init;

		falcon_midi = MIDIOut(0);
		falcon_osc = NetAddr("127.0.0.1", 8890);

		create_boid = MGU_parameter(container, \create_boid,
			Integer, [0, 1], 0, true).parentAccess_(this);

		reset_score = MGU_parameter(container, \reset_score, Integer,
			[0, 1], 0, true).parentAccess_(this);

		boid_density = MGU_parameter(container, \boid_density, Float,
			[0.10, 10.0], 0.86, true).parentAccess_(this);
		boid_grainsize = MGU_parameter(container, \boid_grainsize, Integer,
			[5, 500], 500, true).parentAccess_(this);
		boid_pan = MGU_parameter(container, \boid_pan, Float,
			[-0.99, 1], 0, true).parentAccess_(this);
		boid_note = MGU_parameter(container, \boid_note, Integer,
			[0, 5], 0, true).parentAccess_(this);
		boid_gain = MGU_parameter(container, \boid_gain, Float,
			[0, 2], 1, true).parentAccess_(this);

		granpad_note = MGU_parameter(container, \granpad_note, Integer,
			[0, 3], 0, true).parentAccess_(this);
		granpad_gain = MGU_parameter(container, \granpad_gain, Float,
			[0, 2], 1, true).parentAccess_(this);

		gransects_note = MGU_parameter(container, \gransects_note, Integer,
			[69, 127], 100, true).parentAccess_(this);
		gransects_gain = MGU_parameter(container, \gransects_gain, Float,
			[0, 2], 1, true).parentAccess_(this);

		bassfm_note = MGU_parameter(container, \bassfm_note, Integer,
			[0, 3], 0, true).parentAccess_(this);
		bassfm_gain = MGU_parameter(container, \bassfm_gain, Float,
			[0, 2], 1, true).parentAccess_(this);

		noise_note = MGU_parameter(container, \noise_note, Integer,
			[95, 127], 95, true).parentAccess_(this);
		noise_gain = MGU_parameter(container, \noise_gain, Float,
			[0, 2], 1, true).parentAccess_(this);

		description = ("superCollider de canards...\n ceci n'est pas très règlementaire..");

		granpad_scale = [72, 71, 69, 67, 65, 64, 62, 60, 59, 57, 55, 53, 52, 50, 48];
		bassfm_scale = [47, 45, 43, 41, 40, 38, 36];

	}

	paramCallBack { |param, value|

		value = value[0];

		switch(param)

		{\reset_score} {

			"[SCORE] RESET".postln;

			6.do({|i|
				falcon_midi.allNotesOff(i)
			});


			"echo gmeason | sudo -S pkill -9 tbupddwu".unixCmd;

		}

		// boids

		{\create_boid} { // boid creation

			var scale = [59, 57, 55, 53, 52, 50, 48];
			var rdm_note = scale.choose;

			if(value == 1) {
				fork({
					falcon_midi.noteOn(5, rdm_note, 64);
					falcon_midi.noteOn(4, [1,2,3].choose, 64);
					0.5.wait();
					falcon_midi.noteOff(5, rdm_note, 0);
				})
			}
		}

		{\boid_density} {
			falcon_osc.sendMsg("/uvi/Part 4/Program/Layer 0/Keygroup 3/Oscillator/Density", value)}

		{\boid_grainsize} {
			falcon_osc.sendMsg("/uvi/Part 4/Program/Layer 0/Keygroup 3/Oscillator/GrainSize", value)}

		{\boid_pan} {
			falcon_osc.sendMsg("/uvi/Part 4/Program/Layer 0/Keygroup 3/Pan", value)}

		{\boid_note} { falcon_midi.noteOn(4, value, 100)}
		{\boid_gain} { falcon_osc.sendMsg("/uvi/Part 4/Program/Gain", value)}

		// pads & synths

		{\granpad_note} {

			switch(value)

			{0} {falcon_midi.allNotesOff(0)}
			{1} {falcon_midi.noteOn(0, 69, 100)}
			{2} { // pick random higher note than current one
				var chosen_note = MGU_arrayLib.pickHigherNote(granpad_scale, granpad_playing);
				granpad_playing = chosen_note;
				falcon_midi.noteOn(0, chosen_note, 64);
			}
			{3} { // pick random lower note than current one
				var chosen_note = MGU_arrayLib.pickLowerNote(granpad_scale, granpad_playing);
				granpad_playing = chosen_note;
				falcon_midi.noteOn(0, chosen_note, 64);
			};

		}

		{\granpad_gain} { falcon_osc.sendMsg("/uvi/Part 0/Program/Gain", value)}

		{\gransects_note} { falcon_midi.noteOn(1, value, 100)}
		{\gransects_gain} { falcon_osc.sendMsg("/uvi/Part 1/Program/Gain", value)}

		{\bassfm_note} {

			switch(value)

			{0} {falcon_midi.allNotesOff(2)}
			{1} {falcon_midi.noteOn(2, 34, 64)}
			{2} { // pick random higher note than current one
				var chosen_note = MGU_arrayLib.pickHigherNote(bassfm_scale, bassfm_playing);
				bassfm_playing = chosen_note;
				falcon_midi.noteOn(2, chosen_note, 64);
			}
			{3} { // pick random lower note than current one
				var chosen_note = MGU_arrayLib.pickLowerNote(bassfm_scale, bassfm_playing);
				bassfm_playing = chosen_note;
				falcon_midi.noteOn(2, chosen_note, 64);
			};
		}

		{\bassfm_gain} { falcon_osc.sendMsg("/uvi/Part 2/Program/Gain", value)}

		{\noise_note} { falcon_midi.noteOn(3, value, 100)}
		{\noise_gain} { falcon_osc.sendMsg("/uvi/Part 3/Program/Gain", value)};


	}

}