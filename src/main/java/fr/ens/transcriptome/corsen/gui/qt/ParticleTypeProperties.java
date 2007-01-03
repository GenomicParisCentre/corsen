/*
 *                      Nividic development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the microarray platform
 * of the École Normale Supérieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Nividic project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/nividic
 *
 */

package fr.ens.transcriptome.corsen.gui.qt;

import java.util.Iterator;
import java.util.Properties;

import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QTableWidget;
import com.trolltech.qt.gui.QTableWidgetItem;

import fr.ens.transcriptome.corsen.calc.ParticleType;

public class ParticleTypeProperties {

  private QDialog mainWindow;
  private Properties properties;
  private Properties defaultProperties;
  private String description;

  //
  // Getters
  //

  /**
   * Get the properties
   * @return Returns the properties
   */
  public Properties getProperties() {

    return properties;
  }

  //
  // Other methods
  //

  @SuppressWarnings("unused")
  public void configureDialog() {
    // Make the dialog.

    System.out.println("coucou");

    final Ui_ParticleTypePropertiesDialog dialogUi = new Ui_ParticleTypePropertiesDialog();
    QDialog dialog = new QDialog(this.mainWindow);
    dialogUi.setupUi(dialog);

    QTableWidget table = dialogUi.propertiesTableWidget;

    table.setColumnCount(2);
    table.setHorizontalHeaderItem(0, new QTableWidgetItem("Key"));
    table.setHorizontalHeaderItem(1, new QTableWidgetItem("Value"));
    table.setVerticalHeader(null);

    if (this.defaultProperties != null) {

      table.setRowCount(this.defaultProperties.size());

      Iterator it = this.defaultProperties.keySet().iterator();
      int count = 0;

      while (it.hasNext()) {

        final String key = (String) it.next();

        String value;

        if (this.properties.containsKey(key))
          value = this.properties.getProperty(key);
        else
          value = this.defaultProperties.getProperty(key);

        table.setItem(count, 0, new QTableWidgetItem(key));
        table.setItem(count, 1, new QTableWidgetItem(value));

        count++;
      }

    }

    dialogUi.descriptionLabel.setText(this.description);

    if (dialog.exec() == QDialog.DialogCode.Accepted.value()) {

      for (int i = 0; i < table.rowCount(); i++)
        this.properties.setProperty(table.item(i, 0).text(), table.item(i, 1)
            .text());

    }

  }

  //
  // Constructor
  //

  /**
   * public constructor.
   */
  ParticleTypeProperties(final QDialog mainWindow,
      final ParticleType particleType, final Properties properties) {

    this.mainWindow = mainWindow;
    this.properties = properties;
    this.defaultProperties = particleType.getDefaultProperties();
    this.description = particleType.getFullDescription();

  }

}
