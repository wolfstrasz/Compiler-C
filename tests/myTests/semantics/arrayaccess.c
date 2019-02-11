#include "Tests array accesses"

struct st {
    int i;
};

struct st st1;
void main (){

    int array[3];
    char *p;
    char ch;
    int a;
    array[0];
    array[1];
    array[2];
    a = ch == "hello"[0];
    a = a == array[0];
    ch = p[0];

    array[0] = a[0]; // error
    a = array['c']; // error
    a = st1.i;      
    a = st1[0]; //error


}