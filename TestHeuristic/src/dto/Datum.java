package dto;

public class Datum {
  long code;
  int score = -1;
  Solver route;
  
  public Datum(long code) {
    this.code = code;
    this.route = new Solver(1);
  }
  
  public Datum(long code, Solver route) {
    this.code = code;
    this.route = new Solver(route);
  }
  
  public int score(byte[] da, byte[] db, byte[] dc)
  {
    if(score == -1){
      score = 0;
      long pos = 0L;
      for(int i = 0;i < 16;i++){
        pos |= (long)i<<4*(int)(code>>>4*i&15);
      }
      {
        int code = 0;
        code = code<<4|(int)(pos>>>4*12&15);
        code = code<<4|(int)(pos>>>4*8&15);
        code = code<<4|(int)(pos>>>4*4&15);
        code = code<<4|(int)(pos>>>4*1&15);
        code = code<<4|(int)(pos>>>4*0&15);
        code = code<<4|(int)(pos>>>4*15&15);
        score += da[code];
      }
      {
        int code = 0;
        code = code<<4|(int)(pos>>>4*7&15);
        code = code<<4|(int)(pos>>>4*6&15);
        code = code<<4|(int)(pos>>>4*5&15);
        code = code<<4|(int)(pos>>>4*3&15);
        code = code<<4|(int)(pos>>>4*2&15);
        code = code<<4|(int)(pos>>>4*15&15);
        score += db[code];
      }
      {
        int code = 0;
        code = code<<4|(int)(pos>>>4*14&15);
        code = code<<4|(int)(pos>>>4*13&15);
        code = code<<4|(int)(pos>>>4*11&15);
        code = code<<4|(int)(pos>>>4*10&15);
        code = code<<4|(int)(pos>>>4*9&15);
        code = code<<4|(int)(pos>>>4*15&15);
        score += dc[code];
      }
    }
    
    return score;
  }

}
