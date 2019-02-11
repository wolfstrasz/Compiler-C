#include "testing valueAt"

struct st {
    int a;
};
void f (){

}

char *f2(){
    char * ch;
    return ch;
}


void main (){
    int *p;
    int *p2;
    int a;
    char *str;
    char ch;
    struct st* struc;
    *p = 1;
    p = p2;
    p2 = *a; // error
    *str = 'c';

    *str = "string"[0];
    ch = *("string"[0]); // error
    ch = *"string"[0]; // error
    ch = *f();// error
    ch = *f2();
    a = (*struc).a;
    a = *struc.a; //error
}