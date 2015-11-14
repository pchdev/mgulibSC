#include "SC_PlugIn.h"
#include <cstdlib>
#include <ctime>
#include <cmath>

static InterfaceTable *ft;

struct Noiser : public Unit {

};

static void Noiser_next_a(Noiser *unit, int inNumSamples);
static void Noiser_Ctor(Noiser *unit);
static void Noiser_Dtor(Noiser *unit);

void Noiser_Ctor(Noiser *unit) {
	
	if(INRATE(0) == calc_FullRate) {
		SETCALC(Noiser_next_a);
	}
	
	srand(time(NULL));
	Noiser_next_a(unit, 1);
	
}

void Noiser_Dtor(Noiser *unit) {

}

void Noiser_next_a(Noiser *unit, int inNumSamples) {
	
	float *out1 = OUT(0);
	
	double rdm1, rdm2, rdm3, noise;
	const static int q = 15;
	const static float c1 = (1 << q) - 1;
	const static float c2 = ((int)(c1 / 3)) + 1;
	const static float c3 = 1.f / c1;
	
	for(int i = 0; i< inNumSamples; i++) {
		
		rdm1 = ((double)rand() / (double)(RAND_MAX + 1.0f));
		rdm2 = ((double)rand() / (double)(RAND_MAX + 1.0f));
		rdm3 = ((double)rand() / (double)(RAND_MAX + 1.0f));
		
		noise = (2 * ((rdm1 * c2) + (rdm2 * c2) + (rdm3 * c2)) -3 * (c2 - 1)) * c3;	
		out1[i] = noise;
	}
	
}

PluginLoad(Noiser) {
	ft = inTable;
	DefineDtorUnit(Noiser);
}