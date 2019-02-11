#include "test typecasting"

struct structure {
    int t;
};

struct structure st;
struct structure *stp;
struct structure starr[100];

void main(){
    char c;
    int i;
    char *cp;
    int *ip;
    int iarr[100];
    char carr[100];
    // TOTAL ERROR COUNT = 18
    //Test int casting
    i = (int)c;
    i = (int)st; // error
    i = (int)stp; //error
    i = (int)cp; // error
    i = (int)ip; // error

    //Test pointer casting
    ip = (int*)cp;
    ip = (int *) stp;
    stp = (struct structure*)cp;
    stp = (struct structure*)ip;
    cp = (char*)ip;
    cp = (char*)stp;
    ip = (int*)c;   // error
    cp = (char*)c; // error
    stp = (struct structure*)c; // error
    ip = (int*)mcmalloc(100);
    cp = (char*)mcmalloc(100);
    stp = (struct structure *)mcmalloc(100);

    // Test pointer casting from ARRAY
    ip = (int*)iarr;
    cp = (char*)carr;
    stp = (struct structure*)starr;
    ip = (void*)iarr; // error as assign statement INT* = VOID*
    ip = (int*)carr; // error (NOT RECOGNISED)
    cp = (char*)starr; // error (NOT RECOGNISED)
    stp = (struct structure*)iarr; // error (NOT RECOGNISED)

    // Test other casting
    c = (char)i; // error (NOT RECOGNISED)
    st = (struct structure)i; // error (NOT RECOGNISED)
    i = (void)i; // error; (NOT RECOGNISED)

    stp = (struct structure*)i; // error
    cp = (char *)i; // error 
    *(void *)ip = *mcmalloc(100); // error as assign (VOID = ...)
    (void *)ip = mcmalloc(100); // error as assign (Typecasting(...) = ...)

}