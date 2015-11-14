
s.options.numOutputBusChannels = 34;
s.boot;

c = MGU_audioTester(s, noiseAmp: 0.025, outputArray: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13]);
c = MGU_audioTester(s, noiseAmp: 0.025, outputArray: [1, 2]);

c.playArray;
c.stopArray;

c.hitLength_(1.5);

c.outputArray = [1, 2, 3, 4];
c.outputArray = [5, 6, 7, 8];
c.outputArray = [7, 8, 9, 10];
c.outputArray = [9, 10, 31, 32];
c.outputArray = [9, 10, 23, 24];
c.outputArray = [23, 24, 25, 26];
c.outputArray = [25, 26, 27, 28];
c.outputArray = [27, 28, 29, 30];
c.outputArray = [29, 30, 31, 32];


g = MGU_audioConfig(s);.
g.initUI;

// config Eclats

s.options.numOutputBusChannels = 20;
s.boot;
c = MGU_audioTester(s, 1, 10, 0.5, 0.2);
c.playArray;
c.play;