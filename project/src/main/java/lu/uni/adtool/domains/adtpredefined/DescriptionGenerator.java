package lu.uni.adtool.domains.adtpredefined;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.SandNode;

public class DescriptionGenerator {
  public static String generateDescription(AdtDomain<?> d, String name, String valueDomain,
      String[] oper) {
    ADTNode node = new ADTNode();
    node.setType(ADTNode.Type.AND_PRO);
    Ring proDefault = ((Ring) d.getDefaultValue(node));
    boolean proModifiable = d.isValueModifiable(node);
    node.setType(ADTNode.Type.AND_OPP);
    Ring opDefault = ((Ring) d.getDefaultValue(node));
    boolean opModifiable = d.isValueModifiable(node);
    return "<html><table align=\"left\" border=0>" + "<tr><th align=\"left\">" + name + "</th></tr>"
        + "<tr align=\"left\"><table border=0 cellpadding=7>"
        + "<tr border=0><th align=\"left\">Value&nbsp;domain:</th>" + "    <td colspan=1><nbr>"
        + valueDomain + "</nbr></td></tr></table></tr>" + "<tr><table border=0>"
        + "<tr><th></th><th border=0>proponent</th><td border=0>&nbsp;&nbsp;&nbsp;&nbsp;</td><th border=0>opponent</th></tr>"
        + "<tr><th align=\"left\">or </th><td colspan=2 border=0><b>op&nbsp; </b>" + oper[0]
        + "</td><td border=0><b>oo&nbsp; </b>" + oper[2] + "</td></tr>"
        + "<tr><th align=\"left\">and</th><td colspan=2 border=0><b>ap&nbsp; </b>" + oper[1]
        + "</td><td border=0><b>ao&nbsp; </b>" + oper[3] + "</td></tr>"
        + "<tr><th align=\"left\">counter</th><td colspan=2 border=0><b>cp&nbsp; </b>" + oper[4]
        + "</td><td border=0><b>co&nbsp; </b>" + oper[5] + "</td></tr>"
        + "<tr><th align=\"left\" border=0>default value&nbsp;&nbsp;</th><td colspan=2 border=0>"
        + proDefault.toUnicode() + "</td><td border=0>" + opDefault.toUnicode() + "</td></tr>"
        + "<tr><th align=\"left\" border=0>modifiable</th><td colspan=2 border=0>"
        + (proModifiable ? "Yes" : "No") + "</td>" + "<td border=0>" + (opModifiable ? "Yes" : "No")
        + "</td></tr>" + "</table></tr>" + "<tr align=\"left\"><table border=0 cellpadding=1>"
        + "<tr border=0><th align=\"left\">Class:</th>" + "<td colspan=1><nbr>"
        + d.getClass().getName() + "</nbr></td></tr></table></tr>" + "</table>" + "</html>";
  }

  public static String generateDescription(SandDomain<?> d, String name, String valueDomain,
      String[] oper) {
    SandNode node = new SandNode();
    node.setType(SandNode.Type.AND);
    Ring defaultVal = ((Ring) d.getDefaultValue(node));
    return "<html><table align=\"left\" border=0>" + "<tr><th align=\"left\">" + name + "</th></tr>"
        + "<tr align=\"left\"><table border=0 cellpadding=7>"
        + "<tr border=0><th align=\"left\">Value&nbsp;domain:</th>" + "    <td colspan=1><nbr>"
        + valueDomain + "</nbr></td></tr></table></tr>" + "<tr><table border=0>"
        + "<tr><th></th><th border=0>attacker</th><</tr>"
        + "<tr><th align=\"left\">or </th><td colspan=1 border=0>" + oper[0] + "</td></tr>"
        + "<tr><th align=\"left\">and</th><td colspan=1 border=0>" + oper[1] + "</td></tr>"
        + "<tr><th align=\"left\">sand</th><td colspan=1 border=0>" + oper[2] + "</td></tr>"
        + "<tr><th align=\"left\" border=0>default value&nbsp;&nbsp;</th><td colspan=1 border=0>"
        + defaultVal.toUnicode() + "</td></tr>" + "</table></tr>"
        + "<tr align=\"left\"><table border=0 cellpadding=1>"
        + "<tr border=0><th align=\"left\">Class:</th>" + "<td colspan=1><nbr>"
        + d.getClass().getName() + "</nbr></td></tr></table></tr>" + "</table>" + "</html>";
  }

}
