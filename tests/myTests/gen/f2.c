


int set(char row, int col, char mark) {

  print_s((char*)"set\n");
  print_c(row);
  print_c('\n');
  print_i(col);
  print_c('\n');
  print_c(mark);
  print_c('\n');

  return 5000;
}



void main () {
    char r;
    int c;
    char m;
    int s;
    r = read_c();
    c = read_i();
    m = read_c();

    
  print_s((char*)"main\n");
  print_c(r);
  print_c('\n');
  print_i(c);
  print_c('\n');
  print_c(m);
  print_c('\n');
    s=set(r,c,m);
    print_i(s);
    print_c('\n');



}