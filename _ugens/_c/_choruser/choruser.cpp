#include "SC_PlugIn.h"
#include <cmath>
#include <cstdlib>
#include <ctime>

static InterfaceTable *ft;
// pointers to functions in the host

struct Choruser : public Unit {
	
	float mFreq, mPhase, mDepth;
	float mDtime;
	float mFbk;
	float mDrywet;
	double mCurrRdm, mPrevRdm;
	int writepos;
	float *delayline;
	float *ssd_fbk;
	int delayline_maxsize;
	
};

// declare UGen functions
static void Choruser_next_a(Choruser *unit, int inNumSamples);
static void Choruser_Ctor(Choruser *unit);
static void Choruser_Dtor(Choruser *unit);
static double rander();

double rander() {
	
	double rdm1, rdm2, rdm3, noise;
	const static int q = 15;
	const static float c1 = (1 << q) - 1;
	const static float c2 = ((int) (c1 / 3)) + 1;
	const static float c3 = 1.f / c1;
	
	// gaussian white noise generator
	rdm1 = ((double)rand() / (double)(RAND_MAX + 1.0f));
	rdm2 = ((double)rand() / (double)(RAND_MAX + 1.0f));
	rdm3 = ((double)rand() / (double)(RAND_MAX + 1.0f));
	
	noise = (2 * ((rdm1 * c2) + (rdm2 * c2) + (rdm3 * c2))
		 -3 * (c2 - 1)) * c3;
	
	return noise;
	
}

void Choruser_Ctor(Choruser *unit) {
	
	// SET CALCULATION FUNCTION
	SETCALC(Choruser_next_a);
	
	// INIT RAND SEED
	srand(time(NULL));
	
	// INIT STATE VARIABLES
	
	// lf-noise variables
	unit->mFreq = IN0(1);
	unit->mPhase = 0.f;
	unit->mDepth = IN0(2) / 100.f;
	unit->mCurrRdm = rander();
	unit->mPrevRdm = rander();
	
	// delayline variables
	unit->mDtime = (IN0(3) / 1000.f) * SAMPLERATE;
	unit->mFbk = IN0(4) / 100.f;
	unit->mDrywet = IN0(5) / 100.f;
	unit->delayline_maxsize = SAMPLERATE * 0.05f;
	unit->writepos = 0;
	
	// allocate the delay line
	unit->delayline = (float*)RTAlloc(unit->mWorld, unit->delayline_maxsize * sizeof(float));
	memset(unit->delayline, 0, unit->delayline_maxsize * sizeof(float));
	
	// allocate SSDs
	unit->ssd_fbk = (float*)RTAlloc(unit->mWorld, sizeof(float));
	memset(unit->ssd_fbk, 0, sizeof(float));
	
	// calculte one sample of output
	Choruser_next_a(unit, 1);

}

void Choruser_Dtor(Choruser *unit) {
	RTFree(unit->mWorld, unit->delayline);
	RTFree(unit->mWorld, unit->ssd_fbk);
}

void Choruser_next_a(Choruser *unit, int inNumSamples) {
	
	// input & output pointers to buffers
	float *in = IN(0);
	float *outL = OUT(0);
	float *outR = OUT(1);
	
	// creating local variables from struct members
	float phase = unit->mPhase;
	float freq = unit->mFreq;
	float depth = unit->mDepth;
	
	int dtime = unit->mDtime;
	float fbk = unit->mFbk;
	float drywet = unit->mDrywet;
	
	double currRdm = unit->mCurrRdm;
	double prevRdm = unit->mPrevRdm;
	
	float lfnoise;
	float delayed;
	
	float *delayline = unit->delayline;
	float *ssd_fbk = unit->ssd_fbk;
	int writepos = unit->writepos;
	float readpos;
	
	// update control-rate inputs
	float curfreq = IN0(1);
	float curdepth = IN0(2);
	float curdtime = IN0(3);
	float curfbk = IN0(4);
	float curdrywet = IN0(5);
	
	if(freq != curfreq) freq = curfreq;
	if(depth != curdepth) depth = curdepth / 100.f;
	if(dtime != curdtime) dtime = (curdtime / 1000.f) * SAMPLERATE;
	if(fbk != curfbk) fbk = curfbk / 100.f;
	if(drywet != curdrywet) drywet = curdrywet / 100.f;
	
	// DSP LOOP
	
	for(int i=0; i<inNumSamples; ++i) {
		
		// ramper
		unsigned char phase_trig;
		phase += (freq / SAMPLERATE);
		phase = fmod(phase, 1.f);
		phase_trig = phase <= (freq / SAMPLERATE);
		
		// lfnoise
		if(phase_trig) {
			prevRdm = currRdm;
			currRdm = rander();
		}
		
		lfnoise = prevRdm + ((currRdm - prevRdm) * phase);
		lfnoise += 1.f;
		lfnoise /= 2.f;
		
		// read / write delay phasors
		readpos = dtime * lfnoise;
		
		writepos++;
		if(writepos >= dtime) writepos = 0;
		
		// writing to delayline
		delayline[(int)writepos] = in[i] + (ssd_fbk[0] * fbk);
		
		// reading from delayline
		delayed = delayline[(int)readpos];
		
		// writing to feedback ssd
		ssd_fbk[0] = delayed;
		
		//outL[i] = (1 - sqrt(lfnoise)) * delayed;
		//outR[i] = sqrt(lfnoise) * delayed;
		
		outL[i] = delayed;
		
	}
	
	// store values back to the struct
	
	unit->mPhase = phase;
	unit->mFreq = freq;
	unit->mDepth = depth;
	unit->mDtime = dtime;
	unit->mFbk = fbk;
	unit->mDrywet = drywet;
	unit->mCurrRdm = currRdm;
	unit->mPrevRdm = prevRdm;
	
	unit->writepos = writepos;
	
}

PluginLoad(Choruser)
{
	ft = inTable; // store pointer to InterfaceTable
	DefineDtorUnit(Choruser); // when delays involved, otherwise DefineSimpleUnit()
	
}

