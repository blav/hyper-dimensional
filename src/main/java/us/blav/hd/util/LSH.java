package us.blav.hd.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LSH {

  private int numHashes; // number of hash functions to use
  private int hashSize; // size of each hash in bits
  private int numBands; // number of bands to use
  private int bandSize; // size of each band in hash functions
  private List<Map<Integer, List<Integer>>> hashTables; // list of hash tables

  public LSH(int numHashes, int hashSize, int numBands) {
    this.numHashes = numHashes;
    this.hashSize = hashSize;
    this.numBands = numBands;
    this.bandSize = numHashes / numBands;

    // initialize the hash tables
    hashTables = new ArrayList<>();
    for (int i = 0; i < numBands; i++) {
      hashTables.add(new HashMap<>());
    }
  }

  public void train(List<boolean[]> data) {
    // generate random hash functions
    Random rand = new Random();
    int[][] hashFunctions = new int[numHashes][data.get(0).length];
    for (int i = 0; i < numHashes; i++) {
      for (int j = 0; j < data.get(0).length; j++) {
        hashFunctions[i][j] = rand.nextInt(2) * 2 - 1;
      }
    }

    // split the hash functions into bands
    int[][] bands = new int[numBands][bandSize];
    for (int i = 0; i < numBands; i++) {
      System.arraycopy(hashFunctions, i * bandSize, bands[i], 0, bandSize);
    }

    // hash the data and add it to the appropriate hash table
    for (int i = 0; i < data.size(); i++) {
      boolean[] vector = data.get(i);
      for (int j = 0; j < numBands; j++) {
        int[] bandHash = new int[bandSize];
        System.arraycopy(hashFunctions, j * bandSize, bandHash, 0, bandSize);
        int hashValue = 0;
        for (int k = 0; k < bandSize; k++) {
          if (vector[k]) {
            hashValue += bandHash[k];
          }
        }
        Map<Integer, List<Integer>> table = hashTables.get(j);
        if (!table.containsKey(hashValue)) {
          table.put(hashValue, new ArrayList<>());
        }
        table.get(hashValue).add(i);
      }
    }
  }

  public List<Integer> query(boolean[] vector) {
    List<Integer> candidates = new ArrayList<>();
    for (int i = 0; i < numBands; i++) {
      int[] bandHash = new int[bandSize];
      for (int j = 0; j < bandSize; j++) {
        bandHash[j] = vector[i * bandSize + j] ? 1 : -1;
      }
      int hashValue = 0;
      for (int j = 0; j < bandSize; j++) {
        hashValue += bandHash[j];
      }
      Map<Integer, List<Integer>> table = hashTables.get(i);
      if (table.containsKey(hashValue)) {
        candidates.addAll(table.get(hashValue));
      }
    }
    return candidates;
  }
}
