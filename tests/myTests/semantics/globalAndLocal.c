#include "tests global and local scopes"

char var;

void f (){

    int var;
    int var2;
}


int main(){

    var = 'c';
    var2 = 10;  //error
}