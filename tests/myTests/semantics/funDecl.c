#include "Testing functions"

void f1(){
    return ;
}

void f2(){  // error

    return 1;
}

int f3(){
    return 1;
}

int f4(){    // error
    return;
}

char *f5(){
    char * ch;
    return ch;
}
char *f6(){     // error
    int  * ch;
    return ch;
}

void f10(int a, char c){
    int b;
}

void main (){
    f10(1,'c');
    f10(); // error
    f10(1); // error
    f10(1,1); // error
    f10('c', 1); // error

}