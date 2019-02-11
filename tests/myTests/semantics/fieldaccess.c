#include "Testing field access"

struct str {
    int a;
    int *b;
    int c[100];
};

struct str  {   //error
    int d;
};

void main(){

    char c;
    int i;
    struct str k;
    k.a;
    k.b;
    k.c[0];

    k.d; //error
    i= k.a;
    i= *k.b;
    i= k.c[0];
    c= k.a;//error
    c= *k.b; // error
    c= k.c[0]; // error
     
}