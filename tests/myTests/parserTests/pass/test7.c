#include "stdlib.h"
struct _struct {
    char s1;
    char s2;
};
void voidling(){

}
int main(){

	//int(*(*foo)void)[3];
    int a;
    int b;
    int c;
    int d;
    int e;
    char f;
    int math1;
    int math2;
    int math3;
    int math4;
    int bool0;
    int bool1;
    int bool2;
    int bool3;
	a = (int)-'0';
	b = -(int)'0';					
	c = -(int)-'0';
	d = -(int)-(char)sizeof(int);
	e = (int)(char)-(int)'0' * (int)(sizeof(struct _struct) - 2*sizeof(char));
	f = -(-((char)(((int)'0'))));
    
    math1 = 1 + 2 * 3;
    math2 = (1 + 2) * 3;
    math3 = 1 * 2 + 3;
    math4 = (1 * 2) + 3;

    bool0 = (0 && 0 || 1);
    bool1 = (1 || 0 && 0);
    bool2 = ((1 || 0) && 0);
    bool3 = (1 || (0 && 0));
    printf("OKI");

    return 0;
}