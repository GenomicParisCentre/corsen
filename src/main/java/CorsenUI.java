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
import javax.swing.filechooser.FileFilter;

public class CorsenUI extends JFrame {

  private JPanel jContentPane = null;

  private File arnFile = null;
  private File mitoFile = null;
  private File dirFile = null;

  private JLabel dirLabel = new JLabel("no derectory set");
  private JLabel arnLabel = new JLabel("no file set");
  private JLabel mitoLabel = new JLabel("no file set");
  private JTextField cubeSizeTextField = new JTextField("");
  private JTextField zCoefTextField = new JTextField("");
  private JCheckBox zCoefCheckBox = new JCheckBox();
  private JTextField pixelSizeTextField = new JTextField("");
  private JLabel statusBar = new JLabel("Ready");

  private JButton startButton = new JButton("Start");
  private File currentDirectory;
  private FileFilter ff;

  public void addActionListener(ActionListener al) {

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

  public float getCubeLen() {

    String s = cubeSizeTextField.getText();
    if (s == null)
      return 0.0f;

    try {
      return Float.parseFloat(s);
    } catch (NumberFormatException e) {

      return 0.0f;
    }
  }

  public void setCubeLen(final float cubeSize) {

    this.cubeSizeTextField.setText("" + cubeSize);
  }

  public float getPixelSize() {

    String s = pixelSizeTextField.getText();
    if (s == null)
      return 0.0f;

    try {
      return Float.parseFloat(s);
    } catch (NumberFormatException e) {

      return 0.0f;
    }
  }

  public void setPixelSize(final float pixelSize) {

    this.pixelSizeTextField.setText("" + pixelSize);
  }

  public float getZCoef() {

    String s = zCoefTextField.getText();
    if (s == null)
      return 0.0f;

    try {
      return Float.parseFloat(s);
    } catch (NumberFormatException e) {

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
   * @return void
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

    if (jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setLayout(new BorderLayout());

      JPanel superPanel = new JPanel();
      superPanel.setLayout(new BorderLayout());

      superPanel.add(this.startButton, BorderLayout.SOUTH);

      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

      final JPanel panelDir = new JPanel();
      panelDir.setLayout(new BorderLayout());
      panel.add(panelDir);

      panelDir.add(new JLabel("Directory data"), BorderLayout.WEST);
      dirLabel.setEnabled(false);
      dirLabel.setHorizontalAlignment(JLabel.CENTER);
      panelDir.add(dirLabel, BorderLayout.CENTER);

      JButton dirButton = new JButton("Browse");

      dirButton.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
          JFileChooser jfc = new JFileChooser();
          // jfc.addChoosableFileFilter(ff);
          jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

          if (getCurrentDirectory() != null)
            jfc.setCurrentDirectory(getCurrentDirectory());

          int result = jfc.showOpenDialog(panelDir);
          if (result == JFileChooser.APPROVE_OPTION) {
            dirFile = jfc.getSelectedFile();
            dirLabel.setText(dirFile.getName());
            currentDirectory = dirFile.getParentFile();
          }

        }
      });

      panelDir.add(dirButton, BorderLayout.EAST);

      final JPanel panelMessagers = new JPanel();
      panelMessagers.setLayout(new BorderLayout());
      panel.add(panelMessagers);

      panelMessagers.add(new JLabel("Messager data"), BorderLayout.WEST);
      arnLabel.setEnabled(false);
      arnLabel.setHorizontalAlignment(JLabel.CENTER);
      panelMessagers.add(arnLabel, BorderLayout.CENTER);

      JButton arnButton = new JButton("Browse");

      arnButton.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
          JFileChooser jfc = new JFileChooser();
          jfc.addChoosableFileFilter(ff);

          if (getCurrentDirectory() != null)
            jfc.setCurrentDirectory(getCurrentDirectory());

          int result = jfc.showOpenDialog(panelMessagers);
          if (result == JFileChooser.APPROVE_OPTION) {
            arnFile = jfc.getSelectedFile();
            arnLabel.setText(arnFile.getName());
            currentDirectory = arnFile.getParentFile();
          }

        }
      });

      panelMessagers.add(arnButton, BorderLayout.EAST);

      JPanel panelMito = new JPanel();
      panelMito.setLayout(new BorderLayout());
      panel.add(panelMito);

      panelMito.add(new JLabel("Mito data"), BorderLayout.WEST);
      mitoLabel.setEnabled(false);
      mitoLabel.setHorizontalAlignment(JLabel.CENTER);
      panelMito.add(mitoLabel, BorderLayout.CENTER);

      JButton mitoButton = new JButton("Browse");

      mitoButton.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
          JFileChooser jfc = new JFileChooser();
          jfc.addChoosableFileFilter(ff);
          if (getCurrentDirectory() != null)
            jfc.setCurrentDirectory(getCurrentDirectory());

          int result = jfc.showOpenDialog(panelMessagers);
          if (result == JFileChooser.APPROVE_OPTION) {
            mitoFile = jfc.getSelectedFile();
            mitoLabel.setText(mitoFile.getName());
            currentDirectory = mitoFile.getParentFile();
          }

        }
      });

      panelMito.add(mitoButton, BorderLayout.EAST);

      JPanel panelCubeSize = new JPanel();
      panelCubeSize.setLayout(new BorderLayout());
      panel.add(panelCubeSize);

      panelCubeSize.add(new JLabel("Cube size: "), BorderLayout.WEST);
      panelCubeSize.setEnabled(false);
      cubeSizeTextField.setHorizontalAlignment(JLabel.RIGHT);
      panelCubeSize.add(cubeSizeTextField, BorderLayout.EAST);

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
      JPanel panelCoefZ = new JPanel();
      panelCoefZ.setLayout(new BorderLayout());
      panel.add(panelCoefZ);

      panelCoefZ.add(zCoefCheckBox, BorderLayout.WEST);
      panelCoefZ.add(new JLabel("Z Coef: "), BorderLayout.CENTER);
      panelCoefZ.setEnabled(false);
      zCoefTextField.setHorizontalAlignment(JLabel.RIGHT);

      panelCoefZ.add(zCoefTextField, BorderLayout.EAST);

      JPanel panelPixelSize = new JPanel();
      panelPixelSize.setLayout(new BorderLayout());
      panel.add(panelPixelSize);

      panelPixelSize.add(new JLabel("Pixel size: "), BorderLayout.WEST);
      panelPixelSize.setEnabled(false);
      pixelSizeTextField.setHorizontalAlignment(JLabel.RIGHT);
      panelPixelSize.add(pixelSizeTextField, BorderLayout.EAST);

      superPanel.add(panel, BorderLayout.CENTER);
      jContentPane.add(superPanel, BorderLayout.CENTER);
      jContentPane.add(this.statusBar, BorderLayout.SOUTH);

    }
    return jContentPane;
  }

  //
  // Constructor
  //

  public CorsenUI(final FileFilter ff) {
    this();
    this.ff = ff;
  }

  public static void main(String[] args) {

    new CorsenUI().show();

  }

}
