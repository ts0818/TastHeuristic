package dto;

import java.util.Arrays;

public class Solver {
  long[] a;
  static final int w = 2; // width
  int p = 0; // index pointer
  int b = 0; // bit pointer
  int sz = 0; // size
  
  public Solver(Solver o) {
    this.p = o.p;
    this.b = o.b;
    this.sz = o.sz;
    a = Arrays.copyOf(o.a, o.a.length);
  }
  public Solver(int n) { a = new long[n]; sz = 0; }
  public Solver add(int n)
  {
    if(p+1 >= a.length && b+w >= 64 || p >= a.length)a = Arrays.copyOf(a, a.length*3/2+1);
    for(int i = 0;i < w;i++){
      if(n<<~i<0)a[p] |= 1L<<b;
      if(++b >= 64){
        b -= 64;
        p++;
      }
    }
    sz++;
    return this;
  }
  public int size() { return sz; }
  public Solver clear() { p = 0; sz = 0; b = 0; return this; }
  public int[] toArray() {
    int[] ret = new int[sz];
    int lp = 0, lb = 0, lsz = 0;
    while(lp < p || lp == p && lb < b){
      for(int i = 0;i < w;i++){
        if(a[lp]<<~lb<0)ret[lsz] |= 1<<i;
        if(++lb >= 64){
          lb -= 64;
          lp++;
        }
      }
      lsz++;
    }
    return ret;
  }

}
