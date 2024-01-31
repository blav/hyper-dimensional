package us.blav.hd.util;

import jakarta.inject.Inject;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import us.blav.hd.BinaryKDSpace;
import us.blav.hd.BinaryVector;
import us.blav.hd.Hyperspace;
import us.blav.hd.InjectionExtension;

import static java.lang.Math.pow;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith ({ MockitoExtension.class, InjectionExtension.class })
class KDTreeTest {

  @Inject
  private Hyperspace.Factory factory;

  @Test
  public void contains_should_return_true_for_all_added_points_in_3D_space () {
    Random random = new Random ();
    List<XYZPoint> points = IntStream.range (0, 1000)
      .mapToObj (ignore -> new XYZPoint (random.nextDouble (1.0), random.nextDouble (1.0), random.nextDouble (1.0)))
      .collect (Collectors.toList ());

    XYZSpace space = new XYZSpace ();
    KDTree<XYZPoint> tree = new KDTree<> (space, points);
    assertThat (points.stream ().allMatch (tree::contains)).isTrue ();
  }

  @Test
  public void count_of_k_nearest_neighbors_should_be_roughly_equal_to_k_in_3D_space () {
    Random random = new Random ();
    List<XYZPoint> points = IntStream.range (0, 1000)
      .mapToObj (ignore -> new XYZPoint (random.nextDouble (1.0), random.nextDouble (1.0), random.nextDouble (1.0)))
      .collect (Collectors.toList ());

    XYZSpace space = new XYZSpace ();
    KDTree<XYZPoint> tree = new KDTree<> (space, points);
    int deviation = IntStream.range (1, 1000)
      .map (k -> tree.nearestNeighbourSearch (points.getFirst (), k).size () - k)
      .sum ();

    assertThat (deviation).isLessThan (3);
  }

  @Test
  public void count_of_k_nearest_neighbors_should_be_roughly_equal_to_k_in_binary_space () {
    Hyperspace hyperspace = factory.create (100);
    int count = 1000;
    List<BinaryVector> points = IntStream.range (0, count)
      .mapToObj (ignore -> hyperspace.newRandom ())
      .collect (Collectors.toList ());

    KDTree<BinaryVector> tree = new KDTree<> (new BinaryKDSpace (hyperspace), points);

    assertThat (points.stream ().allMatch (tree::contains)).isTrue ();
    double deviation = IntStream.range (0, count)
      .mapToDouble (k -> tree.nearestNeighbourSearch (points.getFirst (), k + 1).size () - k)
      .sum () / pow (count, 2) / 2.;

    assertThat (deviation).isLessThan (.05);
  }

  @Test
  public void nearest_neighbors_should_return_closest_vector () {
//    Hyperspace hyperspace = factory.create (100);
//    int count = 1000;
//    List<BinaryVector> points = IntStream.range (0, count)
//      .mapToObj (ignore -> hyperspace.newRandom ())
//      .toList ();
//
//    KDTree<BinaryVector> tree = new KDTree<> (new BinaryKDSpace (hyperspace), points);
//    BinaryVector vector = points.getFirst ();
//    BinaryVector close = vector.duplicate ();
//    close.bits ().flip (0);
//
//    assertThat (tree.nearestNeighbourSearch (close, 5))
//      .contains (vector);
  }
}