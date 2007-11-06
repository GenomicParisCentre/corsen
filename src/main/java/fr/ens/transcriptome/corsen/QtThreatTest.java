package fr.ens.transcriptome.corsen;

import com.trolltech.qt.QThread;
import com.trolltech.qt.core.QEventLoop;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.Qt.ConnectionType;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;

public class QtThreatTest extends QDialog {

  private QLabel label = new QLabel("Hello world !!!");

  private class Messenger extends QObject {

    public QObject.Signal1<String> messageSignal = new QObject.Signal1<String>();
  }

  private class MyThread implements Runnable {

    public Messenger messenger;

    public void run() {

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      messenger.messageSignal.emit("Hello world from MyThread !!!");
    }
  }

  @SuppressWarnings("unused")
  private void updateLabel(String message) {

    this.label.setText(message);
  }

  public QtThreatTest() {

    QGridLayout layout = new QGridLayout();
    layout.addWidget(label);
    this.setLayout(layout);

    Messenger mes = new Messenger();
    mes.messageSignal.connect(this, "updateLabel(String)",
        ConnectionType.QueuedConnection);

    MyThread mt = new MyThread();
    mt.messenger = mes;

    Thread t = new Thread(mt);
    mes.moveToThread(t);

    t.start();
    System.out.println("Thead running...");
  }

  public static void main(final String[] args) {
    QApplication.initialize(args);
    new QtThreatTest().show();
    QApplication.exec();
  }
}
