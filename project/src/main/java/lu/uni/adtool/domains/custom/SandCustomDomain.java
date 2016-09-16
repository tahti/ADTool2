package lu.uni.adtool.domains.custom;

public interface SandCustomDomain {

  public boolean setAnd(String expr);
  public boolean setSand(String expr);
  public boolean setOr(String expr);
  public boolean setName(String name);
  public boolean setDescription(String description);
  public boolean setDefault(String value);

  public String getName();
  public String getDescription();
  public String getShortDescription();
  public String getAnd();
  public String getSand();
  public String getOr();
  public String getDefault();
}
