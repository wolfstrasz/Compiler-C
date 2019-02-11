#include "test recursion"

int f(){
    return f();
}

int a(){
    return a(1); // error
}

int c(int b){
    return c(3); 
}

int main(){

    return 0;   
}