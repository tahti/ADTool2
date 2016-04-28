package lu.uni.adtool.domains;

/**
 * If domain takes parameter to constructor it should implement this interface.
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
