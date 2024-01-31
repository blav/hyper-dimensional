package us.blav.hd;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import lombok.Getter;
import lombok.NonNull;
import us.blav.hd.util.KDTree;
import us.blav.hd.util.Pair;

public class AssociativeMemory<METADATA> {

  private final Map<BinaryVector, METADATA> metadata;

  private final KDTree<BinaryVector> tree;

  @Getter
  private final Hyperspace hyperspace;

  public static class Factory {

    @Inject
    private BinaryKDSpace.Factory kdSpaceFactory;

    <METADATA> AssociativeMemory<METADATA> create (Hyperspace hyperspace) {
      return new AssociativeMemory<> (kdSpaceFactory, hyperspace);
    }
  }

  @Inject
  public AssociativeMemory (BinaryKDSpace.Factory kdSpaceFactory, @Assisted @NonNull Hyperspace hyperspace) {
    this.hyperspace = hyperspace;
    this.metadata = new HashMap<> ();
    this.tree = new KDTree<> (kdSpaceFactory.create (hyperspace));
  }

  public AssociativeMemory<METADATA> add (BinaryVector vector, METADATA metadata) {
    this.metadata.put (vector, metadata);
    this.tree.add (vector);
    return this;
  }

  public AssociativeMemory<METADATA> add (Map<BinaryVector, METADATA> vectors) {
    this.metadata.putAll (vectors);
    vectors.keySet ().forEach (tree::add);
    return this;
  }

  public Optional<Pair<BinaryVector, METADATA>> lookup (BinaryVector vector) {
    return tree.nearestNeighbourSearch (vector, 1)
      .stream ()
      .findFirst ()
      .map (v -> new Pair<> (v, metadata.get (v)));
  }
}
