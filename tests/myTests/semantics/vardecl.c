#include "Test type analysis for vardecl"

struct MyStruct {
    int a;
};

int main (){


    int i;
    int * i_ptr;
    int iArray[10];
    int *array_i_ptr[10];

    char c;
    char *c_pointer ;
    char cArray [10];
    char * array_c_ptr[10];

    struct MyStruct s;
    struct MyStruct* s_ptr;
    struct MyStruct sArray[20];
    struct MyStruct * array_s_ptr[300];

    // Invalid
    void v; // error
    void varr[100]; //error
    void *vp; // error
    void * array_v_ptr [300]; // error

}