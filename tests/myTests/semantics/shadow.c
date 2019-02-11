#include "tests shadowing"

int var;

int main(){

    var = 10;

    {
        char var;
        var = 'c';
    }

    var = 5;
}