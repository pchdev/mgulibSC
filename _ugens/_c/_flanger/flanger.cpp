#include "SC_PlugIn.h"

static InterfaceTable *ft;

struct Flanger : public Unit {
	float rate, delaysize, fwdhop, readpos;
	int writepos;
	
	// a pointer to the memory we'll use for our internal delay
	float *delayline;

};

static void Flanger_Ctor(Flanger *unit);
static void Flanger_next(Flanger *unit, int inNumSamples);
static void Flanger_Dtor(Flanger *unit);

// Constructor

void Flanger_Ctor(Flanger *unit) {
	
	// 1. set calculation function
	SETCALC(Flanger_next);
	
	// 2. init state variables
	unit->delaysize = SAMPLERATE * 0.02f;
	// reference to control-rate/scalar-rate inputs : 1 = rate in .sc
	float rate = IN0(1);
	float delta = (unit->delaysize * rate) / SAMPLERATE;
	unit->fwdhop = delta + 1.0f;
	unit->rate = rate;
	
	// allocate the delay line
	unit->delayline = (float*)RTAlloc(unit->mWorld, unit->delaysize * sizeof(float));
	// init to zeroes
	memset(unit->delayline, 0, unit->delaysize * sizeof(float));
	
	// 3. calculate 1 sample's worth of output
	Flanger_next(unit, 1);
	
}

// real-time calculations

void Flanger_next(Flanger *unit, int inNumSamples) {
	
	float *in = IN(0);
	float *out = OUT(0);
	
	float depth = IN0(2);
	
	// copying into local variables -> improves efficiency, the C++ optimizer will cause the values to be loaded into registers
	
	float rate = unit->rate;
	float fwdhop = unit->fwdhop;
	float readpos = unit->readpos;
	int writepos = unit->writepos;
	int delaysize = unit->delaysize;
	
	float val, delayed, currate;
	
	currate = IN0(1);
	
	if(rate != currate) {
		// rate input needs updating
		rate = currate;
		fwdhop = ((delaysize * rate * 2) / SAMPLERATE) + 1.0f;
	}
	
	for(int i=0; i < inNumSamples; i++) {
		// take each sample from input
		val = in[i];
		
		// write to the delay line
		delayline[writepos++] = val;
		if(writepos == delaysize) writepos = 0;
		
		// read from the delay line
		delayed = delayline[(int)readpos];
		readpos += fwdhop;
		
		// update position, NB we may be moving forwards or backwards
		while((int)readpos >= delaysize)
			readpos -= delaysize;
		while((int)readpos < 0)
			readpos += delaysize;
		
		// mix dry and wet together, and output them
		out[i] = val + (delayed * depth);
	}
	
	unit->writepos = writepos;
	unit->readpos = readpos;
	
}

// Destructor 	

void Flanger_Dtor(Flanger *unit) {
	RTFree(unit->mWorld, unit->delayline);
}

// called by host when plugin loaded

PluginLoad(Flanger) {
	ft = inTable; // store pointer to InterfaceTable
	//DefineSimpleUnit(Flanger);
	DefineDtorUnit(Flanger); // for memory allocation
}



