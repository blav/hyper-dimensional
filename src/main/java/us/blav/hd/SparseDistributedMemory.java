package us.blav.hd;

import jakarta.inject.Inject;

import java.util.Collection;
import java.util.stream.IntStream;

import com.google.inject.assistedinject.Assisted;
import us.blav.hd.util.KDTree;

public class SparseDistributedMemory {

  public interface Factory {

    SparseDistributedMemory create (Hyperspace hyperspace, int hardLocations);

  }

  public interface Selection {

    void write ();

    BinaryVector read ();

  }

  public class Space implements KDTree.KDSpace<Bundler> {

    @Override
    public int getK () {
      return hyperspace.dimensions ();
    }

    @Override
    public double distance (Bundler o1, Bundler o2) {
      return IntStream.range (0, hyperspace.dimensions ())
        .mapToDouble (i -> Math.abs (o1.counter (i) - o2.counter (i)))
        .sum ();
    }

    @Override
    public double difference (Bundler o1, Bundler o2, int axis) {
      return o1.counter (axis) - o2.counter (axis);
    }
  }

  private final KDTree<Bundler> tree;

  private final Hyperspace hyperspace;

  @Inject
  public SparseDistributedMemory (
    @Assisted Hyperspace hyperspace,
    @Assisted int hardLocations
  ) {
    this.hyperspace = hyperspace;
    tree = new KDTree<> (new Space ());
    IntStream.range (0, hardLocations)
      .mapToObj (ignore -> hyperspace.newBundler ().add (hyperspace.newRandom ()))
      .forEach (tree::add);
  }

  public Selection select (BinaryVector vector, int count) {
    Bundler center = hyperspace.newBundler ().add (vector);
    Collection<Bundler> neighbours = tree.nearestNeighbourSearch (center, count);
    return new Selection () {
      @Override
      public void write () {
        neighbours.stream ()
          .filter (b -> b != center)
          .forEach (b -> b.add (vector));
      }

      @Override
      public BinaryVector read () {
        Bundler bundler = hyperspace.newBundler ();
        neighbours.stream ()
          .filter (b -> b != center)
          .map (Bundler::reduce)
          .forEach (bundler::add);

        return bundler.reduce ();
      }
    };
  }
}
