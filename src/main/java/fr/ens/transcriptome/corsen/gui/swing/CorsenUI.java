/*
 *                  Corsen development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU General Public Licence version 2 or later. This
 * should be distributed with the code. If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Copyright for this code is held jointly by the microarray platform
 * of the École Normale Supérieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Corsen project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/corsen
 *
 */

package fr.ens.transcriptome.corsen.gui.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

public class CorsenUI extends JFrame {

  /**
   * 
   */
  private static final long serialVersionUID = -1337483720840258361L;

  private JPanel jContentPane = null;

  private File arnFile = null;
  private File mitoFile = null;
  private File dirFile = null;

  private final JLabel dirLabel = new JLabel("no derectory set");
  private final JLabel arnLabel = new JLabel("no file set");
  private final JLabel mitoLabel = new JLabel("no file set");
  private final JTextField zCoefTextField = new JTextField("");
  private final JCheckBox zCoefCheckBox = new JCheckBox();
  private final JTextField pixelSizeTextField = new JTextField("");
  private final JLabel statusBar = new JLabel("Ready");

  private final JButton startButton = new JButton("Start");
  private File currentDirectory;
  private FileFilter ff;

  public void addActionListener(final ActionListener al) {

    this.startButton.addActionListener(al);
  }

  public File getMitoFile() {
    return this.mitoFile;
  }

  public File getARNFile() {
    return this.arnFile;
  }

  public File getDirFile() {
    return this.dirFile;
  }

  public File getCurrentDirectory() {
    return this.currentDirectory;
  }

  public float getPixelSize() {

    final String s = this.pixelSizeTextField.getText();
    if (s == null)
      return 0.0f;

    try {
      return Float.parseFloat(s);
    } catch (final NumberFormatException e) {

      return 0.0f;
    }
  }

  public void setPixelSize(final float pixelSize) {

    this.pixelSizeTextField.setText("" + pixelSize);
  }

  public float getZCoef() {

    final String s = this.zCoefTextField.getText();
    if (s == null)
      return 0.0f;

    try {
      return Float.parseFloat(s);
    } catch (final NumberFormatException e) {

      return 0.0f;
    }
  }

  public void setZCoef(final float zCoef) {

    this.zCoefTextField.setText("" + zCoef);
  }

  public boolean isUpdateZ() {
    return this.zCoefCheckBox.isSelected();
  }

  /**
   * Enable the start button
   * @param value The state of activation of the start button
   */
  public void setStartEnable(final boolean value) {
    if (this.startButton != null)
      this.startButton.setEnabled(value);
  }

  public void showError(final String error) {

    JOptionPane.showMessageDialog(getJContentPane(), error, "Error",
        JOptionPane.ERROR_MESSAGE);
  }

  public void setStatusMessage(final String message) {

    if (message == null)
      return;
    this.statusBar.setText(message);
  }

  /**
   * This is the default constructor
   */
  public CorsenUI() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   */
  private void initialize() {
    this.setSize(300, 200);
    this.setContentPane(getJContentPane());
    this.setTitle("JFrame");
  }

  /**
   * This method initializes jContentPane
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {

    if (this.jContentPane == null) {
      this.jContentPane = new JPanel();
      this.jContentPane.setLayout(new BorderLayout());

      final JPanel superPanel = new JPanel();
      superPanel.setLayout(new BorderLayout());

      superPanel.add(this.startButton, BorderLayout.SOUTH);

      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

      final JPanel panelDir = new JPanel();
      panelDir.setLayout(new BorderLayout());
      panel.add(panelDir);

      panelDir.add(new JLabel("Directory data"), BorderLayout.WEST);
      this.dirLabel.setEnabled(false);
      this.dirLabel.setHorizontalAlignment(SwingConstants.CENTER);
      panelDir.add(this.dirLabel, BorderLayout.CENTER);

      final JButton dirButton = new JButton("Browse");

      dirButton.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
          final JFileChooser jfc = new JFileChooser();
          // jfc.addChoosableFileFilter(ff);
          jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

          if (getCurrentDirectory() != null)
            jfc.setCurrentDirectory(getCurrentDirectory());

          final int result = jfc.showOpenDialog(panelDir);
          if (result == JFileChooser.APPROVE_OPTION) {
            CorsenUI.this.dirFile = jfc.getSelectedFile();
            CorsenUI.this.dirLabel.setText(CorsenUI.this.dirFile.getName());
            CorsenUI.this.currentDirectory =
                CorsenUI.this.dirFile.getParentFile();
          }

        }
      });

      panelDir.add(dirButton, BorderLayout.EAST);

      final JPanel panelMessagers = new JPanel();
      panelMessagers.setLayout(new BorderLayout());
      panel.add(panelMessagers);

      panelMessagers.add(new JLabel("Messager data"), BorderLayout.WEST);
      this.arnLabel.setEnabled(false);
      this.arnLabel.setHorizontalAlignment(SwingConstants.CENTER);
      panelMessagers.add(this.arnLabel, BorderLayout.CENTER);

      final JButton arnButton = new JButton("Browse");

      arnButton.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
          final JFileChooser jfc = new JFileChooser();
          jfc.addChoosableFileFilter(CorsenUI.this.ff);

          if (getCurrentDirectory() != null)
            jfc.setCurrentDirectory(getCurrentDirectory());

          final int result = jfc.showOpenDialog(panelMessagers);
          if (result == JFileChooser.APPROVE_OPTION) {
            CorsenUI.this.arnFile = jfc.getSelectedFile();
            CorsenUI.this.arnLabel.setText(CorsenUI.this.arnFile.getName());
            CorsenUI.this.currentDirectory =
                CorsenUI.this.arnFile.getParentFile();
          }

        }
      });

      panelMessagers.add(arnButton, BorderLayout.EAST);

      final JPanel panelMito = new JPanel();
      panelMito.setLayout(new BorderLayout());
      panel.add(panelMito);

      panelMito.add(new JLabel("Mito data"), BorderLayout.WEST);
      this.mitoLabel.setEnabled(false);
      this.mitoLabel.setHorizontalAlignment(SwingConstants.CENTER);
      panelMito.add(this.mitoLabel, BorderLayout.CENTER);

      final JButton mitoButton = new JButton("Browse");

      mitoButton.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
          final JFileChooser jfc = new JFileChooser();
          jfc.addChoosableFileFilter(CorsenUI.this.ff);
          if (getCurrentDirectory() != null)
            jfc.setCurrentDirectory(getCurrentDirectory());

          final int result = jfc.showOpenDialog(panelMessagers);
          if (result == JFileChooser.APPROVE_OPTION) {
            CorsenUI.this.mitoFile = jfc.getSelectedFile();
            CorsenUI.this.mitoLabel.setText(CorsenUI.this.mitoFile.getName());
            CorsenUI.this.currentDirectory =
                CorsenUI.this.mitoFile.getParentFile();
          }

        }
      });

      panelMito.add(mitoButton, BorderLayout.EAST);

      /*
       * JButton mitoButton = new JButton("Browse");
       * mitoButton.addActionListener(new ActionListener() { public void
       * actionPerformed(final ActionEvent e) { JFileChooser jfc = new
       * JFileChooser(); jfc.addChoosableFileFilter(ff); if
       * (getCurrentDirectory() != null)
       * jfc.setCurrentDirectory(getCurrentDirectory()); int result =
       * jfc.showOpenDialog(panelMessagers); if (result ==
       * JFileChooser.APPROVE_OPTION) { mitoFile = jfc.getSelectedFile();
       * mitoLabel.setText(mitoFile.getName()); currentDirectory =
       * mitoFile.getParentFile(); } } });
       */

      // panelCubeSize.add(mitoButton, BorderLayout.EAST);
      final JPanel panelCoefZ = new JPanel();
      panelCoefZ.setLayout(new BorderLayout());
      panel.add(panelCoefZ);

      panelCoefZ.add(this.zCoefCheckBox, BorderLayout.WEST);
      panelCoefZ.add(new JLabel("Z Coef: "), BorderLayout.CENTER);
      panelCoefZ.setEnabled(false);
      this.zCoefTextField.setHorizontalAlignment(SwingConstants.RIGHT);

      panelCoefZ.add(this.zCoefTextField, BorderLayout.EAST);

      final JPanel panelPixelSize = new JPanel();
      panelPixelSize.setLayout(new BorderLayout());
      panel.add(panelPixelSize);

      panelPixelSize.add(new JLabel("Pixel size: "), BorderLayout.WEST);
      panelPixelSize.setEnabled(false);
      this.pixelSizeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
      panelPixelSize.add(this.pixelSizeTextField, BorderLayout.EAST);

      superPanel.add(panel, BorderLayout.CENTER);
      this.jContentPane.add(superPanel, BorderLayout.CENTER);
      this.jContentPane.add(this.statusBar, BorderLayout.SOUTH);

    }
    return this.jContentPane;
  }

  //
  // Constructor
  //

  public CorsenUI(final FileFilter ff) {
    this();
    this.ff = ff;
  }

  public static void main(final String[] args) {

    new CorsenUI().show();

  }

}
