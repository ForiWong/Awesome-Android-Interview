package com.wlp.myanjunote.java.thread.threadinteraction;

public class Main {
  public static void main(String[] args) {
//    runThreadInteractionDemo();
    runWaitDemo();
  }

  static void runThreadInteractionDemo() {
    new ThreadInteractionDemo().runTest();
  }

  static void runWaitDemo() {
    new WaitDemo().runTest();
  }
}
