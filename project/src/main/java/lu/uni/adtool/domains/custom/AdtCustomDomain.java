package lu.uni.adtool.domains.custom;

public interface AdtCustomDomain {

  public boolean setCp(String expr);
  public boolean setCo(String expr);
  public boolean setAp(String expr);
  public boolean setAo(String expr);
  public boolean setOp(String expr);
  public boolean setOo(String expr);
  public boolean setName(String name);
  public boolean setDescription(String description);
  public boolean setProDefault(String value);
  public boolean setOppDefault(String value);
  public void setProModifiable(boolean value);
  public void setOppModifiable(boolean value);

  public String getName();
  public String getDescription();
  public String getShortDescription();
  public String getCp();
  public String getCo();
  public String getAp();
  public String getAo();
  public String getOp();
  public String getOo();
  public String getProDefault();
  public String getOppDefault();
  public boolean isProModifiable();
  public boolean isOppModifiable();
}
