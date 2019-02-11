#include "test statements"


int f(){
    int a;
    return a;
}

void main(){
    int a;
    int arr[100];
    int barr[100];
    while (a == 1){

    }
    while (1){

    }
    while ((int)'c'){

    }
    while ('c'){ // error
    }

    if ( a == 1) {

    }
    if ( a ){

    } else {
        f();
    }

    while (f()){}

    a = 1;
    (void)a = *(mcmalloc(100)); // error
    arr = barr; //error

}