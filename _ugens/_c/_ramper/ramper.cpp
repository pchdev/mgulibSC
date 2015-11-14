#include "SC_PlugIn.h"


// contains pointers to functions in the host (server).
static InterfaceTable *ft;

// declare struct to hold unit generator state
struct MGU_Ramper : public Unit
{
	double mPhase; // phase of the oscillator, from 0 to 1
	float mFreqMul; // a constant for multiplying frequency
};

// UGen functions prototypes
static void MGU_Ramper_next_a(MGU_Ramper *unit, int inNumSamples);
static void MGU_Ramper_next_k(MGU_Ramper *unit, int inNumSamples);
static void MGU_Ramper_Ctor(MGU_Ramper *unit);

// Ctor is called to initialize the unit generator.

// It only executes once.
// Does basically 3 things :
// 1. set the calculation function
// 2. initialize the unit generator state variables.
// 3. calculate one sample of output

void MGU_Ramper_Ctor(MGU_Ramper* unit) {
	
	// 1. calculation function
	
	if(INRATE(0) == calc_FullRate) {
		// if audio rate
		SETCALC(MGU_Ramper_next_a);
	} else { // if control rate (or scalar)
		SETCALC(MGU_Ramper_next_k);
	};
	
	// 2.  initialize UGen state variables
	
	// constant for multiplying the frequency
	unit->mFreqMul = 2.0 * SAMPLEDUR;
	
	// get initial phase of oscillator
	unit->mPhase = IN0(1);
	
	// 3. calculate one sample of output
	MGU_Ramper_next_k(unit, 1);
	
}

void MGU_Ramper_next_a(MGU_Ramper *unit, int inNumSamples){
	
	// pointer to output buffer
	float *out = OUT(0);
	
	// pointer to input buffer
	float *freq = IN(0);
	
	// get phase and freqmul constants from struct and store it in a local variable. The optmizer will cause them to be loaded into a register.
	
	float freqmul = unit->mFreqMul;
	double phase = unit->mPhase;
	
	// perform a loop for the number of samples in the control period.
	// if audio rate : inNumSamples will be 64 or whatever. If control rate : inNumSamples will be 1.
	
	for(int i=0; i < inNumSamples; i++) {
		
		// out must be written last for in place operation
		float z = phase;
		phase += freq[i] * freqmul;
		
		// these if statements wrap the phase a +1 or -1.
		if(phase >= 1.f) phase -= 2.f;
		else if (phase <= -1.f) phase += 2.f;
		
		// write the output
		out[i] = z;
	}
	
	// store the phase back to the struct
	unit->mPhase = phase;
	
}

void MGU_Ramper_next_k(MGU_Ramper *unit, int inNumSamples) {
	
	float *out = OUT(0);
	float freq = IN(0) * unit->mFreqMul;
	
	double phase = unit->mPhase;
	
	if (freq >= 0.f) {
		// positive freq
		for (int i=0; i < inNumSamples; i++) {
			out[i] = phase;
			phase += freq;
			if (phase >= 1.f) phase -= 2.f;
		}
	} else {
		// neg freq
		for (int i=0; i < inNumSamples; i++) {
			out[i] = phase;
			phase += freq;
			if (phase <= -1.f) phase += 2.f;
		}
	}
	
	unit->mPhase = phase;
}


PluginLoad(MGU_Ramper) {
	ft = inTable;
	DefineSimpleUnit(MGU_Ramper);MGU_Ramper	
} 