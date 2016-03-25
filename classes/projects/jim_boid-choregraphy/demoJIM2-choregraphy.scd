//------------------------------------
//                                   |
// I-SCORE DEMO - JIM 2016 ALBI      |
// THEO DE LA HOGUE - PIERRE COCHARD |
//                                   |
//------------------------------------

// see .scorejson file for i-score
// and processing file for flock simulation

(

m = MGU_minuitInterface("boid_audio", 9998);
m.respAddr_("192.168.1.16", 13579);

f = JIM_falconInterface();

f.registerToMinuit(m);
f.generateUI();

)

f.generateUI();

// 192.168.1.16 -> ip théo
// scale = f mode


OSCFunc.trace(false);


