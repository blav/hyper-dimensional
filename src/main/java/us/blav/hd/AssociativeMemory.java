package us.blav.hd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import lombok.Getter;
import lombok.NonNull;

public class AssociativeMemory<METADATA> {

  private final List<Map.Entry<BinaryVector, METADATA>> vectors;

  @Getter
  private final Hyperspace hyperspace;
  public static class Factory {

    <METADATA> AssociativeMemory<METADATA> create (Hyperspace hyperspace) {
      return new AssociativeMemory<> (hyperspace);
    }
  }

  @Inject
  public AssociativeMemory (@Assisted @NonNull Hyperspace hyperspace) {
    this.hyperspace = hyperspace;
    this.vectors = new ArrayList<> ();
  }

  public AssociativeMemory<METADATA> add (BinaryVector vector, METADATA metadata) {
    this.vectors.add (Map.entry (vector, metadata));
    return this;
  }

  public AssociativeMemory<METADATA> add (Map<BinaryVector, METADATA> vectors) {
    this.vectors.addAll (vectors.entrySet ());
    return this;
  }

  public Map.Entry<BinaryVector, METADATA> lookup (BinaryVector vector) {
    if (vectors.isEmpty ())
      throw new IllegalStateException ();

    Double similarity = null;
    Map.Entry<BinaryVector, METADATA> nearest = null;
    Metric metric = hyperspace.cosine ();
    for (Map.Entry<BinaryVector, METADATA> entity : vectors) {
      double s = metric.apply (vector, entity.getKey ());
      if (similarity == null || s > similarity) {
        similarity = s;
        nearest = entity;
      }
    }

    return nearest;
  }
}
