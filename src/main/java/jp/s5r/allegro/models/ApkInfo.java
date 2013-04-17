package jp.s5r.allegro.models;

import java.net.URI;
import java.util.Date;

public class ApkInfo {
  private String title;
  private URI uri;
  private long fileSize;
  private Date lastModified;

  public ApkInfo(String title, URI uri, long fileSize, Date lastModified) {
    this.title = title;
    this.uri = uri;
    this.fileSize = fileSize;
    this.lastModified = lastModified;
  }

  @Override
  public String toString() {
    return title;
  }

  public String getTitle() {
    return title;
  }

  public URI getUri() {
    return uri;
  }

  public long getFileSize() {
    return fileSize;
  }

  public Date getLastModified() {
    return lastModified;
  }
}
