package lu.uni.adtool.ui.printview;

import lu.uni.adtool.ui.MainController;

import java.awt.BorderLayout;
import java.awt.print.Pageable;

import javax.swing.JDialog;

public class JPrintPreviewDialog extends JDialog
{

  /**
   *
   */
  private static final long serialVersionUID = -8452285246945202151L;

  /**
   * Constructs a new instance.
   */
  public JPrintPreviewDialog(MainController controller, final Pageable pageable)
  {
    super(controller.getFrame(), true);
    JPrintPreviewPane preview = new JPrintPreviewPane(pageable,this);
    setAlwaysOnTop(true);
    setTitle("Print preview");
    this.setLocationRelativeTo(controller.getFrame());

    getContentPane().add(preview,BorderLayout.CENTER);
    pack();
    setLocation(50,50);
    setSize(800, 600);
  }
}

