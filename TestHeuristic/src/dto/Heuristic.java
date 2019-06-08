package dto;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;

public class Heuristic {
  static InputStream is;
  static PrintWriter out;
  static String INPUT = "";
  
  public static InputStream getIs() {
    return is;
  }

  public static void setIs(InputStream is) {
    Heuristic.is = is;
  }

  public static PrintWriter getOut() {
    return out;
  }

  public static void setOut(PrintWriter out) {
    Heuristic.out = out;
  }

  public static String getINPUT() {
    return INPUT;
  }

  public static void setINPUT(String iNPUT) {
    INPUT = iNPUT;
  }

  public static void solve()
  {
    int n = 4;
    int[][] a = new int[n][n];
    int zr = -1, zc = -1;
    for(int i = 0;i < n;i++){
      for(int j = 0;j < n;j++){
        String x = ns();
        if(x.equals("*")){
          a[i][j] = 15;
          zr = i; zc = j;
        }else{
          a[i][j] = Integer.parseInt(x)-1;
        }
      }
    }
    
    int[][] to = new int[n][n];
    for(int i = 0;i < n;i++){
      for(int j = 0;j < n;j++){
        to[i][j] = i*4+j;
      }
    }
    
    int[] route = search(enc(a), enc(to));
    if(route == null)return;
    
    for(int i = 0;i < route.length;i++){
      int k = route[i];
      int nr = zr + dr[k];
      int nc = zc + dc[k];
      out.println(a[nr][nc]+1);
      a[zr][zc] = a[nr][nc];
      a[nr][nc] = 15;
      zr = nr; zc = nc;
    }
  }
  
  static final int[] dr = { 1, 0, -1, 0 };
  static final int[] dc = { 0, 1, 0, -1 };
  
  static int[] search(long start, long goal)
  {
    final byte[] gdA = dists(goal, new int[]{0, 1, 4, 8, 12});
    final byte[] gdB = dists(goal, new int[]{2, 3, 5, 6, 7});
    final byte[] gdC = dists(goal, new int[]{9, 10, 11, 13, 14});
//    if(true)return null;
    
    List<Datum> q = new ArrayList<Datum>();
    q.add(new Datum(start));
//    int SIZE = 55000;
    int SIZE = 50000;
    int phase = 0;
    LongHashSet pved = new LongHashSet();
    LongHashSet ved = new LongHashSet();
    int[] f = new int[200];
    while(true){
      tr(phase++, q.size());
      List<Datum> nq = new ArrayList<Datum>();
      LongHashSet nved = new LongHashSet();
      for(int i = 0;i < q.size() && i < SIZE;i++){
        Datum cur = q.get(i);
//        if(i < 1){
//          tr(cur.score(grc));
////          if(cur.score(grc) == 1){
////            int[][] ya = dec(cur.code);
////            for(int[] row : ya){
////              tr(row);
////            }
////            tr();
////          }
//        }
        if(cur.score(gdA, gdB, gdC) == 0){
          return cur.route.toArray();
        }
        long c = cur.code;
        int z = Long.numberOfTrailingZeros(c&c>>>1&c>>>2&c>>>3&0x1111111111111111L)>>>2;
        for(int k = 0;k < 4;k++){
          int nr = (z>>>2) + dr[k], nc = (z&3) + dc[k];
          if(nr >= 0 && nr < 4 && nc >= 0 && nc < 4){
            int t = 4*nr+nc;
            long move = (int)(cur.code>>>4*t&15);
            long ncode = (cur.code|15L<<4*t)&~((15^move)<<4*z);
            if(!nved.contains(ncode) && !pved.contains(ncode)){
              nved.add(ncode);
              Datum nex = new Datum(ncode, cur.route);
              nex.route.add(k);
              nq.add(nex);
            }
          }
        }
      }
      
      int scoremin = 999, scoremax = -1;
      for(Datum d : nq){
        d.score(gdA, gdB, gdC);
        f[d.score]++;
        scoremin = Math.min(scoremin, d.score);
        scoremax = Math.max(scoremax, d.score);
      }
      Datum[][] bucket = new Datum[scoremax-scoremin+1][];
      for(int i = scoremin;i <= scoremax;i++){
        bucket[i-scoremin] = new Datum[f[i]];
      }
      for(Datum d : nq){
        bucket[d.score-scoremin][--f[d.score]] = d;
      }
      
      q.clear();
      for(int i = scoremin;i <= scoremax;i++){
        for(Datum d : bucket[i-scoremin]){
          if(q.size() >= SIZE)break;
          q.add(d);
        }
      }
      
//      Collections.sort(nq, new Comparator<Datum>() {
//        public int compare(Datum a, Datum b) {
////          return a.score(grc) - b.score(grc);
//          return
//              a.score(gdA, gdB, gdC) - 
//              b.score(gdA, gdB, gdC);
//        }
//      });
//      tr(nq.get(0).score, nq.get(nq.size()-1).score);
      pved = ved;
      ved = nved;
//      q = nq;
    }
  }
  
  static void check(long c)
  {
    int[] f = new int[16];
    for(int i = 0;i < 4;i++){
      for(int j = 0;j < 4;j++){
        f[(int)(c>>>4*(4*i+j)&15)]++;
      }
    }
    for(int i = 0;i < 16;i++){
      if(f[i] != 1){
        tr(i, f);
        throw new RuntimeException();
      }
    }
  }
  
  static int[] q = new int[6000000];
  
  static byte[] dists(long a, int[] mask)
  {
    int m = mask.length;
    m++;
    byte[] d = new byte[1<<4*m];
    Arrays.fill(d, Byte.MAX_VALUE);
    int start = 0;
    for(int i = 0;i < 16;i++){
      int v = (int)(a>>>4*i&15);
      for(int k = 1;k < m;k++){
        if(mask[k-1] == v){
          start |= i<<4*k;
        }
      }
      if(15 == v){
        start |= i;
      }
    }
    
    int p = 0;
    q[p++] = start;
    d[start] = 0;
    
    int[] wh = new int[16];
    Arrays.fill(wh, -1);
    for(int v = 0;v < p;v++){
      int cur = q[v];
      
      for(int l = 1;l < m;l++){
        wh[cur>>>4*l&15] = l;
      }
      int zr = cur>>>2&3, zc = cur&3;
      for(int k = 0;k < 4;k++){
        int nr = zr + dr[k], nc = zc + dc[k];
        if(nr >= 0 && nr < 4 && nc >= 0 && nc < 4){
          int nex = cur&~(zr*4+zc)|nr*4+nc;
          if(wh[nr*4+nc] >= 1)nex = nex&~(nr*4+nc<<4*wh[nr*4+nc])|zr*4+zc<<4*wh[nr*4+nc];
//          for(int l = 1;l < m;l++){
//            if((cur>>>4*l&15) == nr*4+nc){
//              nex = nex&~(nr*4+nc<<4*l)|zr*4+zc<<4*l;
//            }
//          }
          if(d[nex] > d[cur] + 1){
            d[nex] = (byte)(d[cur] + 1);
            q[p++] = nex;
          }
        }
      }
      for(int l = 1;l < m;l++){
        wh[cur>>>4*l&15] = -1;
      }
    }
    
    return d;
  }
  static long enc(int[][] a)
  {
    long h = 0;
    for(int i = 3;i >= 0;i--){
      for(int j = 3;j >= 0;j--){
        h = h<<4|a[i][j];
      }
    }
    return h;
  }
  
  static int[][] dec(long c)
  {
    int[][] a = new int[4][4];
    for(int i = 0;i < 4;i++){
      for(int j = 0;j < 4;j++){
        a[i][j] = (int)(c&15);
        c>>>=4;
      }
    }
    return a;
  }
  private static byte[] inbuf = new byte[1024];
  static int lenbuf = 0, ptrbuf = 0;
  
  private static int readByte()
  {
    if(lenbuf == -1)throw new InputMismatchException();
    if(ptrbuf >= lenbuf){
      ptrbuf = 0;
      try { lenbuf = is.read(inbuf); } catch (IOException e) { throw new InputMismatchException(); }
      if(lenbuf <= 0)return -1;
    }
    return inbuf[ptrbuf++];
  }
  
  private static boolean isSpaceChar(int c) { return !(c >= 33 && c <= 126); }
  private static int skip() { int b; while((b = readByte()) != -1 && isSpaceChar(b)); return b; }
  
  private static String ns()
  {
    int b = skip();
    StringBuilder sb = new StringBuilder();
    while(!(isSpaceChar(b))){ // when nextLine, (isSpaceChar(b) && b != ' ')
      sb.appendCodePoint(b);
      b = readByte();
    }
    return sb.toString();
  }
  
  public static void tr(Object... o) { if(INPUT.length() != 0)System.out.println(Arrays.deepToString(o)); }

}
