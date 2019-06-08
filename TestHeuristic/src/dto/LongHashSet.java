package dto;

public class LongHashSet {
  public long[] keys;
  public boolean[] allocated;
  private int scale = 1<<2;
  private int rscale = 1<<1;
  private int mask = scale-1;
  public int size = 0;
  
  public LongHashSet(){
    allocated = new boolean[scale];
    keys = new long[scale];
  }
  
  public boolean contains(long x)
  {
    int pos = h(x)&mask;
    while(allocated[pos]){
      if(x == keys[pos])return true;
      pos = pos+1&mask;
    }
    return false;
  }
  
  public boolean add(long x)
  {
    int pos = h(x)&mask;
    while(allocated[pos]){
      if(x == keys[pos])return false;
      pos = pos+1&mask;
    }
    if(size == rscale){
      resizeAndAdd(x);
    }else{
      keys[pos] = x;
      allocated[pos] = true;
    }
    size++;
    return true;
  }
  
  public boolean remove(long x)
  {
    int pos = h(x)&mask;
    while(allocated[pos]){
      if(x == keys[pos]){
        size--;
        // take last and fill rmpos
        int last = pos;
        pos = pos+1&mask;
        while(allocated[pos]){
          int lh = h(keys[pos])&mask;
          // lh <= last < pos
          if(
              lh <= last && last < pos ||
              pos < lh && lh <= last ||
              last < pos && pos < lh
              ){
            keys[last] = keys[pos];
            last = pos;
          }
          pos = pos+1&mask;
        }
        keys[last] = 0;
        allocated[last] = false;
        
        return true;
      }
      pos = pos+1&mask;
    }
    return false;
  }
  
  private void resizeAndAdd(long x)
  {
    int nscale = scale<<1;
    int nrscale = rscale<<1;
    int nmask = nscale-1;
    boolean[] nallocated = new boolean[nscale];
    long[] nkeys = new long[nscale];
    for(int i = next(0);i < scale;i = next(i+1)){
      long y = keys[i];
      int pos = h(y)&nmask;
      while(nallocated[pos])pos = pos+1&nmask;
      nkeys[pos] = y;
      nallocated[pos] = true;
    }
    {
      int pos = h(x)&nmask;
      while(nallocated[pos])pos = pos+1&nmask;
      nkeys[pos] = x;
      nallocated[pos] = true;
    }
    allocated = nallocated;
    keys = nkeys;
    scale = nscale;
    rscale = nrscale;
    mask = nmask;
  }
  
  public int next(int itr)
  {
    while(itr < scale && !allocated[itr])itr++;
    return itr;
  }
  
  private int h(long x)
  {
    x ^= x>>>33;
    x *= 0xff51afd7ed558ccdL;
    x ^= x>>>33;
    x *= 0xc4ceb9fe1a85ec53L;
    x ^= x>>>33;
    return (int)(x^x>>>32);
  }
  
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    for(int i = next(0);i < scale;i = next(i+1)){
      sb.append(",");
      sb.append(keys[i]);
    }
    return sb.length() == 0 ? "" : sb.substring(1);
  }

}
