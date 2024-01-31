package us.blav.hd.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;

import lombok.NonNull;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

/**
 * A k-d tree (short for k-dimensional tree) is a space-partitioning data
 * structure for organizing points in a k-dimensional space. k-d trees are a
 * useful data structure for several applications, such as searches involving a
 * multidimensional search key (e.g. range searches and nearest neighbor
 * searches). k-d trees are a special case of binary space partitioning trees.
 * <p>
 *
 * @author Justin Wetherell <phishman3579@gmail.com>
 * @see <a href="https://en.wikipedia.org/wiki/K-d_tree">K-D Tree (Wikipedia)</a>
 * <br>
 */
public class KDTree<T> implements Iterable<T> {

  public interface KDSpace<T> {

    int getK ();

    double distance (T o1, T o2);

    double difference (T o1, T o2, int axis);

  }

  private final int k;

  private Node root = null;

  private final KDSpace<T> space;

  private final List<Comparator<T>> comparators;

  /**
   * Default constructor.
   */
  public KDTree (KDSpace<T> space) {
    super ();
    this.space = space;
    this.k = space.getK ();
    this.comparators = IntStream.range (0, k)
      .mapToObj (axis -> (Comparator<T>) (v1, v2) -> (int) space.difference (v1, v2, axis))
      .toList ();
  }

  /**
   * Constructor for creating a more balanced tree. It uses the
   * "median of points" algorithm.
   *
   * @param list of XYZPoints.
   */
  public KDTree (KDSpace<T> space, List<T> list) {
    this (space);
    root = createNode (list, k, 0);
  }

  /**
   * Constructor for creating a more balanced tree. It uses the
   * "median of points" algorithm.
   *
   * @param list of XYZPoints.
   * @param k    of the tree.
   */
  public KDTree (KDSpace<T> space, List<T> list, int k) {
    this (space);
    root = createNode (list, k, 0);
  }

  private int compareTo (int depth, int k, T o1, T o2) {
    return comparators.get (depth % k).compare (o1, o2);
  }

  private int compareTo (@NonNull T a, @NonNull T b) {
    return IntStream.range (0, k)
      .mapToObj (comparators::get)
      .map (c -> c.compare (a, b))
      .filter (v -> v != 0)
      .findFirst ()
      .orElse (0);
  }

  /**
   * Creates node from list of XYZPoints.
   *
   * @param list  of XYZPoints.
   * @param k     of the tree.
   * @param depth depth of the node.
   * @return node created.
   */
  private Node createNode (List<T> list, int k, int depth) {
    if (list == null || list.size () == 0)
      return null;

    int axis = depth % k;
    list.sort (comparators.get (axis));

    Node node = null;
    List<T> less = new ArrayList<> (list.size ());
    List<T> more = new ArrayList<> (list.size ());
    if (list.size () > 0) {
      int medianIndex = list.size () / 2;
      node = new Node (list.get (medianIndex), k, depth);
      // Process list to see where each non-median point lies
      for (int i = 0; i < list.size (); i++) {
        if (i == medianIndex)
          continue;
        T p = list.get (i);
        // Cannot assume points before the median are less since they could be equal
        if (compareTo (depth, k, p, node.value) <= 0) {
          less.add (p);
        } else {
          more.add (p);
        }
      }

      if ((medianIndex - 1 >= 0) && less.size () > 0) {
        node.lesser = createNode (less, k, depth + 1);
        node.lesser.parent = node;
      }

      if ((medianIndex <= list.size () - 1) && more.size () > 0) {
        node.greater = createNode (more, k, depth + 1);
        node.greater.parent = node;
      }
    }

    return node;
  }

  /**
   * Adds value to the tree. Tree can contain multiple equal values.
   *
   * @param value T to add to the tree.
   * @return True if successfully added to tree.
   */
  public boolean add (T value) {
    if (value == null)
      return false;

    if (root == null) {
      root = new Node (value);
      return true;
    }

    Node node = root;
    while (true) {
      if (compareTo (node.depth, node.k, value, node.value) <= 0) {
        // Lesser
        if (node.lesser == null) {
          Node newNode = new Node (value, k, node.depth + 1);
          newNode.parent = node;
          node.lesser = newNode;
          break;
        }
        node = node.lesser;
      } else {
        // Greater
        if (node.greater == null) {
          Node newNode = new Node (value, k, node.depth + 1);
          newNode.parent = node;
          node.greater = newNode;
          break;
        }
        node = node.greater;
      }
    }

    return true;
  }

  /**
   * Does the tree contain the value.
   *
   * @param value T to locate in the tree.
   * @return True if tree contains value.
   */
  public boolean contains (T value) {
    if (value == null || root == null)
      return false;

    Node node = getNode (this, value);
    return (node != null);
  }

  /**
   * Locates T in the tree.
   *
   * @param tree  to search.
   * @param value to search for.
   * @return KdNode or NULL if not found
   */
  private Node getNode (KDTree<T> tree, T value) {
    if (tree == null || tree.root == null || value == null)
      return null;

    Node node = tree.root;
    while (true) {
      if (node.value.equals (value)) {
        return node;
      } else if (compareTo (node.depth, node.k, value, node.value) <= 0) {
        // Lesser
        if (node.lesser == null) {
          return null;
        }
        node = node.lesser;
      } else {
        // Greater
        if (node.greater == null) {
          return null;
        }
        node = node.greater;
      }
    }
  }

  /**
   * Removes first occurrence of value in the tree.
   *
   * @param value T to remove from the tree.
   * @return True if value was removed from the tree.
   */
  public boolean remove (T value) {
    if (value == null || root == null)
      return false;

    Node node = getNode (this, value);
    if (node == null)
      return false;

    Node parent = node.parent;
    List<T> nodes = getTree (node);
    if (parent != null) {
      if (node.equals (parent.lesser)) {
        if (nodes.size () > 0) {
          parent.lesser = createNode (nodes, node.k, node.depth);
          if (parent.lesser != null) {
            parent.lesser.parent = parent;
          }
        } else {
          parent.lesser = null;
        }
      } else {
        if (nodes.size () > 0) {
          parent.greater = createNode (nodes, node.k, node.depth);
          if (parent.greater != null) {
            parent.greater.parent = parent;
          }
        } else {
          parent.greater = null;
        }
      }
    } else {
      // root
      if (nodes.size () > 0)
        root = createNode (nodes, node.k, node.depth);
      else
        root = null;
    }

    return true;
  }

  /**
   * Gets the (sub) tree rooted at root.
   *
   * @param root of tree to get nodes for.
   * @return points in (sub) tree, not including root.
   */
  private List<T> getTree (Node root) {
    List<T> list = new ArrayList<> ();
    if (root == null)
      return list;

    if (root.lesser != null) {
      list.add (root.lesser.value);
      list.addAll (getTree (root.lesser));
    }
    if (root.greater != null) {
      list.add (root.greater.value);
      list.addAll (getTree (root.greater));
    }

    return list;
  }

  /**
   * Searches the K nearest neighbor.
   *
   * @param value to find neighbors of.
   * @param count Number of neighbors to retrieve. Can return more than count, if
   *              last nodes are equal distances.
   * @return Collection of T neighbors.
   */
  public Collection<T> nearestNeighbourSearch (T value, int count) {
    if (value == null || root == null)
      return emptyList ();

    // Map used for results
    TreeSet<Node> results = new TreeSet<> (new EuclideanComparator (value));

    // Find the closest leaf node
    Node prev = null;
    Node node = root;
    while (node != null) {
      if (compareTo (node.depth, node.k, value, node.value) <= 0) {
        // Lesser
        prev = node;
        node = node.lesser;
      } else {
        // Greater
        prev = node;
        node = node.greater;
      }
    }
    Node leaf = prev;

    // Used to not re-examine nodes
    Set<Node> examined = new HashSet<> ();

    // Go up the tree, looking for better solutions
    node = leaf;
    while (node != null) {
      // Search node
      searchNode (value, node, count, results, examined);
      node = node.parent;
    }

    // Load up the collection of the results
    Collection<T> collection = new ArrayList<> (count);
    for (Node kdNode : results)
      collection.add (kdNode.value);
    return collection;
  }

  private void searchNode (T value, Node node, int K, TreeSet<Node> results, Set<Node> examined) {
    examined.add (node);

    // Search node
    Node lastNode = null;
    double lastDistance = Double.MAX_VALUE;
    if (results.size () > 0) {
      lastNode = results.last ();
      lastDistance = space.distance (value, lastNode.value);
    }
    Double nodeDistance = space.distance (value, node.value);
    if (nodeDistance.compareTo (lastDistance) < 0) {
      if (results.size () == K && lastNode != null)
        results.remove (lastNode);
      results.add (node);
    } else if (nodeDistance.equals (lastDistance)) {
      results.add (node);
    } else if (results.size () < K) {
      results.add (node);
    }
    lastNode = results.last ();
    lastDistance = space.distance (value, lastNode.value);

    int axis = node.depth % node.k;
    Node lesser = node.lesser;
    Node greater = node.greater;

    // Search children branches, if axis aligned distance is less than
    // current distance
    if (lesser != null && ! examined.contains (lesser)) {
      examined.add (lesser);

      // Continue down lesser branch
      if (space.difference (value, node.value, axis) <= lastDistance)
        searchNode (value, lesser, K, results, examined);
    }
    if (greater != null && ! examined.contains (greater)) {
      examined.add (greater);

      // Continue down greater branch
      if (space.difference (value, node.value, axis) >= - lastDistance)
        searchNode (value, greater, K, results, examined);
    }
  }


  /**
   * Adds, in a specified queue, a given node and its related nodes (lesser, greater).
   *
   * @param node    Node to check. May be null.
   * @param results Queue containing all found entries. Must not be null.
   */
  private void search (final Node node, final Deque<T> results) {
    if (node != null) {
      results.add (node.value);
      search (node.greater, results);
      search (node.lesser, results);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString () {
    return new TreePrinter ().getString (this);
  }

  protected class EuclideanComparator implements Comparator<Node> {

    private final T point;

    public EuclideanComparator (T point) {
      this.point = point;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare (Node o1, Node o2) {
      Double d1 = space.distance (o1.value, point);
      Double d2 = space.distance (o2.value, point);
      if (d1.compareTo (d2) < 0) {
        return - 1;
      } else if (d2.compareTo (d1) < 0) {
        return 1;
      } else {
        return compareTo (o1.value, o2.value);
      }
    }
  }

  /**
   * Searches all entries from the first to the last entry.
   *
   * @return Iterator
   * allowing to iterate through a collection containing all found entries.
   */
  public Iterator<T> iterator () {
    final Deque<T> results = new ArrayDeque<> ();
    search (root, results);
    return results.iterator ();
  }

  /**
   * Searches all entries from the last to the first entry.
   *
   * @return Iterator
   * allowing to iterate through a collection containing all found entries.
   */
  public Iterator<T> reverseIterator () {
    final Deque<T> results = new ArrayDeque<> ();
    search (root, results);
    return results.descendingIterator ();
  }

  private class Node implements Comparable<Node> {

    private final T value;

    private final int k;

    private final int depth;

    private Node parent = null;

    private Node lesser = null;

    private Node greater = null;

    public Node (T value) {
      this.k = 3;
      this.value = value;
      this.depth = 0;
    }

    public Node (T value, int k, int depth) {
      this.k = k;
      this.value = value;
      this.depth = depth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode () {
      return 31 * (this.k + this.depth + this.value.hashCode ());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals (Object obj) {
      return ofNullable (obj)
        .filter (Node.class::isInstance)
        .map (Node.class::cast)
        .map (this::compareTo)
        .map (c -> c == 0)
        .orElse (false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo (Node o) {
      return KDTree.this.compareTo (depth, k, this.value, o.value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString () {
      return "k=" + k +
        " depth=" + depth +
        " id=" + value.toString ();
    }
  }

  protected class TreePrinter {

    public String getString (KDTree<T> tree) {
      if (tree.root == null)
        return "Tree has no nodes.";
      return getString (tree.root, "", true);
    }

    private String getString (Node node, String prefix, boolean isTail) {
      StringBuilder builder = new StringBuilder ();

      if (node.parent != null) {
        String side = "left";
        if (node.parent.greater != null && node.value.equals (node.parent.greater.value))
          side = "right";
        builder.append (prefix).append (isTail ? "└── " : "├── ").append ("[").append (side).append ("] ").append ("depth=").append (node.depth).append (" id=").append (node.value).append ("\n");
      } else {
        builder.append (prefix).append (isTail ? "└── " : "├── ").append ("depth=").append (node.depth).append (" id=").append (node.value).append ("\n");
      }
      List<Node> children = null;
      if (node.lesser != null || node.greater != null) {
        children = new ArrayList<> (2);
        if (node.lesser != null)
          children.add (node.lesser);
        if (node.greater != null)
          children.add (node.greater);
      }
      if (children != null) {
        for (int i = 0; i < children.size () - 1; i++) {
          builder.append (getString (children.get (i), prefix + (isTail ? "    " : "│   "), false));
        }
        if (children.size () >= 1) {
          builder.append (getString (children.get (children.size () - 1), prefix + (isTail ? "    " : "│   "),
            true));
        }
      }

      return builder.toString ();
    }
  }
}
