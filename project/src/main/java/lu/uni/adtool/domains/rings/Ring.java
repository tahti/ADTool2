package lu.uni.adtool.domains.rings;

/**
 * Interface representing abstract ring.
 *
 * @author Piot Kordy
 * @version
 */
public interface Ring extends Comparable<Object> {
  /**
   * Html representatinon of the value represented by this object.
   *
   * @return string with html representation.
   */
  String toUnicode();

  boolean updateFromString(String s);

  int compareTo(Object o);

  Object clone();
}
