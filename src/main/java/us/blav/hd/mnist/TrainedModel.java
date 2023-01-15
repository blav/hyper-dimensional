package us.blav.hd.mnist;

import java.util.List;

import us.blav.hd.BinaryVector;
import us.blav.hd.Metric;
import us.blav.hd.mnist.MNIST.Digit;

public class TrainedModel {

  private final List<BinaryVector> classes;

  private final Model model;

  public TrainedModel (Model model, List<BinaryVector> classes) {
    this.classes = classes;
    this.model = model;
  }

  public int infer (Digit digit) {
    BinaryVector vector = model.encode (digit);
    Double similarity = null;
    int nearest = - 1;
    Metric cosine = model.getHyperspace ().hamming ();
    for (int current = 0; current < classes.size (); current++) {
      double s = cosine.apply (vector, classes.get (current));
      if (similarity == null || s > similarity) {
        similarity = s;
        nearest = current;
      }
    }

    return nearest;
  }
}
