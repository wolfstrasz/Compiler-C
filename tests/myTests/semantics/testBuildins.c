#include "Testing of minic-std.lib"

int main (){

    int a;
    char c;
    char* str;
    char *cv;

    // VALIDS
    a = read_i();
    print_i(a);
    c = read_c();
    print_c(c);
    print_s(str);
   cv = (char*)mcmalloc(sizeof(char));
   cv = (char*)mcmalloc(100);
   str = (char*)mcmalloc(100);
   print_c(str[0]);
    *cv = *(char*)mcmalloc(100);

    read_i();
    read_c();
    mcmalloc(100);

    // INVALID = 5 errors
    a = read_i(a);
    c = read_c(c);
    print_i(c);
    print_c(a);
    cv = mcmalloc(100);


}