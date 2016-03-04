package lu.uni.adtool.tools;

import lu.uni.adtool.ADToolMain;
import lu.uni.adtool.ui.AboutDialog;

import java.awt.Dimension;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

public class Clo {

  public Clo() {
    this.options = new org.apache.commons.cli.Options();
    this.options.addOption("h", "help", false, Options.getMsg("clo.help.txt"));
    this.options.addOption("v", "version", false, Options.getMsg("clo.version.txt"));
    this.options.addOption("a", "all-domains", false, Options.getMsg("clo.allDomains.txt"));
    this.options.addOption("D", "no-derived", false, Options.getMsg("clo.derived.txt"));
    this.options.addOption("m", "mark-editable", false, Options.getMsg("clo.markEditable.txt"));
    this.options.addOption("L", "no-labels", false, Options.getMsg("clo.labels.txt"));
    this.options.addOption("C", "no-computed", false, Options.getMsg("clo.computed.txt"));

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
    option.setArgs(1);
    option.setArgName("domainID");
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
    CommandLineParser parser = new BasicParser();
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
      if (cmd.hasOption("i") && cmd.hasOption("x")) {
        ImportExport exporter = new ImportExport();
        if (cmd.hasOption("a")) {
          exporter.setExportAllDomains(true);
        }
        if (cmd.hasOption("D")) {
          exporter.setNoDerivedValues(true);
        }
        if (cmd.hasOption("m")) {
          exporter.setMarkEditable(true);
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
          if (index > 0){
            try{
              int x = Integer.parseInt(size.substring(0, index));
              int y = Integer.parseInt(size.substring(index +1));
              exporter.setViewPortSize(new Dimension(x, y));
            }
            catch (NumberFormatException e) {
              System.err.println(Options.getMsg("clo.wrongsize"));
            }
          }
        }
        if (cmd.hasOption("d")) {
          String domainId = cmd.getOptionValue("d");
          if (domainId != null) {
            exporter.setExportDomainStr(domainId);
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
    String footer = Options.getMsg("clo.footer.txt");
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
