package main;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;

import dto.Heuristic;

public class TestH {

  public static void main(String[] args) {
    // TODO 自動生成されたメソッド・スタブ
    long S = System.currentTimeMillis();
    
    Heuristic.setIs(Heuristic.getINPUT().isEmpty() ? System.in : new ByteArrayInputStream(Heuristic.getINPUT().getBytes()));
    Heuristic.setOut(new PrintWriter(System.out));
    
    Heuristic.solve();
    Heuristic.getOut().flush();;
    long G = System.currentTimeMillis();
    Object o = G-S+"ms";
    Heuristic.tr(o);

  }

}
