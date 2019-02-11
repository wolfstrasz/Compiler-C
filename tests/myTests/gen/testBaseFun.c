

int f1(){
    return 1;
}

int f2(int arg){

    if(arg == 10){
        return 1000;
    }

    return 1;
}

int f3(int a){

    while(a<1000)return 3;
}

void main(){
    int a;
    a = read_i();

    a = f1() + f2(a);

    print_i(a); // 2 or 1001(if read input is 10)
}