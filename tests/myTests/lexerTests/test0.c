#include"minic-stdlib.h"

// C code that is fully valid in defined Tokens and Grammar
/*  Other test from 1 to X will have simple differences to check whether the Lexer detects tokens
    Tests from Y to Z have 0 INVALID tokens
    Tests written by Boyan Yotov
*/

struct {
    int a,
    char f
} k;

void increase(int a){
    a++;
}

int sum(int a, int b){
    return a+b;
}

void main(){

    char ch = 'c';
    int num0 = 4213;
    int num1A = 500;
    int i = 0;
    int j = 4;
    int array[ 200];
    
    // I goes to 4, when it reaches it k.a=321 and ch = '\t'
    for (i = 0; i <= j; i++){
        if (i <= j){
            num1A = sum(i,j);
        }
        else {
            k.a = 321;
            ch = '\t';
        }
    }
    /* some COMENT */
}
