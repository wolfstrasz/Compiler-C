#include "Test struct declarations"

void _vF (){}
void *_vpF() {}

int _iF() {}
int * _ipF(){}

char _cF() {}
char *_cpF(){}

struct k _skF(){}
struct k * _skpF(){}

void _paramF ( 
				void v,
				void*v,
				int i,
				int *ip,
				char c,
				char *cp,
				struct k sk,
				struct k *skp
				
				/* check arrays? */
				// , void va[1]
				// , void* vap[2]
				// , int ia[3]
				// , int * iap[4]
				// , char ca[5]
				// , char *cap[6]
				// , struct k ska[7]
				// , struct k *skap[8]
				){}

void _blocks() {
	{
		{

		}
	}
}