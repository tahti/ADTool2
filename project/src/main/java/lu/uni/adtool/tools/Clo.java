/**
 * Author: Piotr Kordy (piotr.kordy@uni.lu <mailto:piotr.kordy@uni.lu>) Date:
 * 10/12/2015 Copyright (c) 2015,2013,2012 University of Luxembourg -- Faculty
 * of Science, Technology and Communication FSTC All rights reserved. Licensed
 * under GNU Affero General Public License 3.0; This program is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package lu.uni.adtool.tools;

import lu.uni.adtool.ADToolMain;
import lu.uni.adtool.ui.AboutDialog;

import java.awt.Dimension;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * Class to parse command line options
 */
public class Clo {

  public Clo() {
    this.options = new org.apache.commons.cli.Options();
    this.options.addOption("h", "help", false, Options.getMsg("clo.help.txt"));
    this.options.addOption("v", "version", false, Options.getMsg("clo.version.txt"));
//     this.options.addOption("a", "all-domains", false, Options.getMsg("clo.allDomains.txt"));
    this.options.addOption("D", "no-derived", false, Options.getMsg("clo.derived.txt"));
    this.options.addOption("m", "mark-editable", false, Options.getMsg("clo.markEditable.txt"));
    this.options.addOption("L", "no-labels", false, Options.getMsg("clo.labels.txt"));
    this.options.addOption("C", "no-computed", false, Options.getMsg("clo.computed.txt"));
    // this.options.addOption("r", "rank", false,
    // Options.getMsg("clo.rank.txt"));

    Option option = new Option("o", "open", true, Options.getMsg("clo.open.txt"));
    // Set option c to take 1 to oo arguments
    option.setArgs(Option.UNLIMITED_VALUES);
    option.setArgName("file_1>...<file_N");
    this.options.addOption(option);
    option = new Option("i", "import", true, Options.getMsg("clo.import.txt"));
    option.setArgName("file");
    this.options.addOption(option);
    option = new Option("x", "export", true, Options.getMsg("clo.export.txt"));
    option.setArgName("file");
    this.options.addOption(option);
    option = new Option("d", "domain", true, Options.getMsg("clo.domain.txt"));
//     option.setValueSeparator(',');
//     option.setArgs(1);
    option.setArgName("domainID");
    this.options.addOption(option);
    option = new Option("r", "rank", true, Options.getMsg("clo.rank.txt"));
    option.setArgs(1);
    option.setArgName("rankNo");
    this.options.addOption(option);
    option = new Option("s", "size", true, Options.getMsg("clo.size.txt"));
    option.setArgs(1);
    option.setArgName("X>x<Y");
    this.options.addOption(option);
    this.toOpen = null;
  }

  /**
   * Class used to parse command line options. Returns true if GUI window should
   * be shown
   */
  public boolean parse(String[] args) {
    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = null;
    try {
      cmd = parser.parse(options, args);
      if (cmd.hasOption("h")) {
        help();
        return false;
      }
      else if (cmd.hasOption("v")) {
        version();
        return false;
      }
      else if (cmd.hasOption("o")) {
        this.toOpen = cmd.getOptionValues("o");
        if (this.toOpen != null && this.toOpen.length == 0) {
          this.toOpen = null;
        }
      }
      if (cmd.hasOption("i") && cmd.hasOption("x") || cmd.hasOption("i") && cmd.hasOption("d")
          && (cmd.getOptionValue("d", "0").equals("?") || cmd.getOptionValue("d", "0").equals("q"))) {
        ImportExport exporter = new ImportExport();
        if (cmd.hasOption("D")) {
          exporter.setNoDerivedValues(true);
        }
        if (cmd.hasOption("m")) {
          exporter.setMarkEditable(true);
        }
        if (cmd.hasOption("r")) {
          String r = cmd.getOptionValue("r");
          try {
            int x = Integer.parseInt(r);
            if (x > 0) {
              exporter.setExportRanking(x);
            }
            else {
              System.err.println(Options.getMsg("clo.wrongrank"));
            }
          }
          catch (NumberFormatException e) {
            System.err.println(Options.getMsg("clo.wrongrank"));
          }
        }
        if (cmd.hasOption("L")) {
          exporter.setNoLabels(true);
        }
        if (cmd.hasOption("C")) {
          exporter.setNoComputedValues(true);
        }
        if (cmd.hasOption("s")) {
          String size = cmd.getOptionValue("s");
          int index = size.indexOf('x');
          if (index > 0) {
            try {
              int x = Integer.parseInt(size.substring(0, index));
              int y = Integer.parseInt(size.substring(index + 1));
              exporter.setViewPortSize(new Dimension(x, y));
            }
            catch (NumberFormatException e) {
              System.err.println(Options.getMsg("clo.wrongsize"));
            }
          }
        }
        if (cmd.hasOption("d")) {
          String[] domainIds = cmd.getOptionValues("d");

          if (domainIds != null) {
            // if (domainId == "?" || domainId=="q") {
            // System.out.println(new Integer(exporter.countDomains(fileName)));
            // return false;
            // }
            exporter.setExportDomainStr(domainIds);
          }
        }
        String fileName = cmd.getOptionValue("i");
        if (fileName != null && exporter.doImport(fileName)) {
          fileName = cmd.getOptionValue("x");
          if (fileName != null) {
            exporter.doExport(fileName);
          }
        }
        return toOpen != null;
      }
      if (cmd.getOptions().length > 0) {
        System.err.println(Options.getMsg("clo.wrongCombination") + ".");
        help();
        return false;
      }
    }
    catch (ParseException e) {
      System.err.println(Options.getMsg("clo.parseError") + ": " + e.toString());
      help();
      return false;
    }
    return true;
  }

  public String[] getToOpen() {
    return this.toOpen;
  }

  /**
   * Print usage information
   */
  private void help() {
    // This prints out some help
    HelpFormatter formater = new HelpFormatter();
    formater.setSyntaxPrefix(Options.getMsg("clo.usage.txt") + " ");
    String header = Options.getMsg("clo.header.txt");
    String footer = Options.getMsg("clo.footer.txt", getUsageName());
    // * formatter.printHelp("myapp", header, options, footer, true);

    formater.printHelp(getUsageName() + " ", header, options, footer, true);
  }

  private void version() {
    System.out.println(AboutDialog.getVersionInfo());
  }

  private String getUsageName() {
    String jarName = new java.io.File(
        ADToolMain.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
    if (!jarName.endsWith(".jar")) {
      // Class not from JAR
      return "java -jar <jar file>";
    }
    else {
      return "java -jar " + jarName;
    }
  }

  private org.apache.commons.cli.Options options;
  private String                         toOpen[];
}
