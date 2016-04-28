package lu.uni.adtool.domains.predefined;

/**
 * If domain takes parameter to constructor it should implement this interface. - Obsolete - used to import adt only.
 *
 * @author Piotr Kordy
 */
public interface Parametrized {
  /**
   * get the parameter.
   *
   * @return parameter
   */
  Object getParameter();

  void setParameter(Object o);
}
