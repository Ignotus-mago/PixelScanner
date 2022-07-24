package net.paulhertz.scanner;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Implements a simple Lindenmeyer system (L-system), 
 * a so-called DOL-system: deterministic and context-free.
 * Load production strings into transTable with put(), retrieve them with get().
 * Was used by HilbertScanner until I sped it up more optimized methods. 
 */
public class Lindenmeyer extends Object {
  // our handy tables
  /** transition table for string production */
  private HashMap<String, String> transTable;
  public boolean verbose = false;
  
  
  /**
   * Creates a new Lindenmeyer instance;
   */
  public Lindenmeyer() {
    this.transTable = new HashMap<String, String>();
  }

  /**
   * Gets a value from the transition table corresponding to the supplied key.
   * @param clef   a single-character String
   * @return      value corresponding to the key
   */
  public String get(String clef) {
    if (transTable.containsKey(clef))
      return transTable.get(clef);
    return clef;
  }

  /**
   * Loads a key and its corresponding value into the transition table.
   * @param clef     a single-character String
   * @param value   the String value associated with the key
   */
  public void put(String clef, String value) {
    transTable.put(clef, value);
  }
  
  public void expandString(ArrayList<String> tokens, int levels, ArrayList<String> sb) {
    //System.out.println("level is "+ levels);
    ArrayList<String> temp = new ArrayList<String>();
    for (int i = 0; i < tokens.size(); i++) {
      String ch = tokens.get(i);
      String val = get(ch);
      for (int j = 0; j < val.length(); j++) {
        temp.add("" + val.charAt(j));
      }
    }
    if (levels > 0) {
      expandString(temp, levels - 1, sb);
    }
    else {
      for (int j = 0; j < tokens.size(); j++) {
        sb.add(tokens.get(j));  
      }
    }
  }
  
 
   /**
    * Encode strings representing a Hilbert curve to supplied depth.
    * To draw the actual curve, ignore the R and L symbols
    *     + : 90 degrees CW
    *     - : 90 degrees CCW
    *     F : forward (n) units
    */
   public ArrayList<String> hilbert(int depth) {
     Lindenmeyer lind = new Lindenmeyer();
     lind.put("L", "+RF-LFL-FR+");
     lind.put("R", "-LF+RFR+FL-");
     ArrayList<String> buf = new ArrayList<String>();
     ArrayList<String> seed = new ArrayList<String>();
     seed.add("L");
     lind.expandString(seed, depth, buf);
     if (verbose) {
       System.out.println("Hilbert L-system at depth "+ depth +"\n");
       for (int i = 0; i < buf.size(); i++) {
    	   System.out.print(buf.get(i));
       }
     }
     return buf;
   }
  
  
}