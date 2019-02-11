#include "Testing binary ops"



struct st {
    int b;
};

void main(){
    struct st ststruct;
    int a;
    char *c;
    char *str[10];

    int arr[10];
    a = 10;

    a = a + 4;
    a = a + 'c'; //error
    a = a + (int)'c';
    a = a + (char)1; // typecast error

    a = 1 != 2;
    a = 1 && 2;
    a = 'c' == 'b';
    a = *c == *str[0];
    a = arr[4] == 1; 
    a = arr == 1; // error
    a = ststruct.b == 1;
    a = ststruct == 1; // error
    a = 'c' == 1; // error

    //a = (void)1 == 1; // error  from typescast
    // a = b == 1; // if they want all errors then if "b" is undeclared should return BASETYPE.VOID and wil lresult in error


}