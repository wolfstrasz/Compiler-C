#include "Testing generation"

struct st {
    int a;
    char c;
};

struct at {
    struct st s;
    int si;
};

struct bt {
    int a;
    int* ap;
    int ar[10];
    char ch;
    char *chp;
    char chr[10];
    struct st s;
    struct st * sp;
    struct st sr[10];
};

struct at att;

struct st s;
struct st *sp;
struct st sarr[10];

int a;
int *ap;
int aa[10];

char ch;
char *chp;
char cha[10];

int f (int argI, struct st argSt){
    return 1;
}
int main (){
	int aaaaaaaaaa;

	char charray[13];
	int bbbbbbbbbb;
    int * intp;
    int var1;
    int var2;
	var1 = sizeof(struct bt);
	
    { 
        int var3;
    }
    {
        int var4;
    }
    return 1;
}