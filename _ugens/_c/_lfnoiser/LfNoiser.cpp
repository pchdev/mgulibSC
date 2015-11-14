#include "SC_PlugIn.h"
#include <cstdlib>
#include <ctime>
#include <cmath>

static InterfaceTable *ft;

struct LfNoiser : public Unit {
	float mFreq;
	float mPhase;
	double mCurrRdm, mPrevRdm;
};

static void LfNoiser_next_a(LfNoiser *unit, int inNumSamples);
static void LfNoiser_Ctor(LfNoiser *unit);
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

void LfNoiser_Ctor(LfNoiser *unit) {
	
	SETCALC(LfNoiser_next_a);
	
	srand(time(NULL));
	
	// lfnoise variables
	unit->mFreq = IN0(0);
	unit->mPhase = 0.f;	
	unit->mCurrRdm = rander();
	unit->mPrevRdm = rander();

	LfNoiser_next_a(unit, 1);
}

void LfNoiser_next_a(LfNoiser *unit, int inNumSamples) {
	
	float *out1 = OUT(0);
	float phase = unit->mPhase;
	float freq = unit->mFreq;
	double currRdm = unit->mCurrRdm;
	double prevRdm = unit->mPrevRdm;
	
	double lfnoise;
	
	for(int i = 0; i < inNumSamples; i++) {
		
		// ramper
		unsigned char phase_trig;
		phase += (freq / SAMPLERATE);
		phase = fmod(phase, 1.f);
		phase_trig = phase <= (freq / SAMPLERATE);
		
		if(phase_trig) {
			prevRdm = currRdm;
			currRdm = rander();
		}
		
		lfnoise = prevRdm + ((currRdm - prevRdm) * phase);
		
		out1[i] = lfnoise;
		
	}
	
	unit->mPhase = phase;
	unit->mCurrRdm = currRdm;
	unit->mPrevRdm = prevRdm;
	
}

PluginLoad(LfNoiser) {
	
	ft = inTable;
	DefineSimpleUnit(LfNoiser);
	
}
