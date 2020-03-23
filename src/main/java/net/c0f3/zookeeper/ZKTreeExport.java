package net.c0f3.zookeeper;

import com.dobrunov.zktreeutil.zkExportToFS;
import com.dobrunov.zktreeutil.zkExportToFile;
import com.dobrunov.zktreeutil.zkExportToXmlFile;
import com.dobrunov.zktreeutil.zkTreeJob;

import java.io.File;

/**
 * @author Konstantin.Lychagin
 * created: 04.09.2018
 */
public class ZKTreeExport {
  private String format;
  private File file;
  private String server;

  public ZKTreeExport(
      String format,
      File file,
      String server
  ) {
    this.format = format;
    this.file = file;
    this.server = server;
  }

  public void perform(Runnable callback) {
    Thread t = new Thread(() -> run(callback));
    t.start();
  }

  public void run(Runnable callback) {
    zkTreeJob job;
    switch (format) {
      case ".zk":
        job = new zkExportToFile(server, "", file.getAbsolutePath());
        break;
      case "XML":
        job = new zkExportToXmlFile(server, "", file.getAbsolutePath());
        break;
      case "folder":
        job = new zkExportToFS(server, "", file.getAbsolutePath());
        break;
      default:
        job = () -> {
        };
    }
    job.go();
    callback.run();
  }
}
