package us.blav.hd.sdm;


import jakarta.inject.Inject;

import static us.blav.hd.Injection.getInstance;

public class SDMMain {

  @Inject
  private SDMLibrary library;

  public void run () {
    System.out.println (library.square (1421));
  }

  public static void main (String[] args) {
    getInstance (SDMMain.class).run ();
  }
}