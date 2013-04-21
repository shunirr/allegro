package jp.s5r.allegro.model;

import android.net.Uri;

import java.util.Date;

public class ApkInfo {
  private String title;

  private Uri uri;

  private Date lastModified;

  private int size;

  public ApkInfo() {
  }

  public ApkInfo(final String title,
                 final Uri uri,
                 final Date lastModified,
                 final int size) {
    setTitle(title);
    setUri(uri);
    setLastModified(lastModified);
    setSize(size);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public Uri getUri() {
    return uri;
  }

  public void setUri(final Uri uri) {
    this.uri = uri;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(final Date lastModified) {
    this.lastModified = lastModified;
  }

  public int getSize() {
    return size;
  }

  public void setSize(final int size) {
    this.size = size;
  }
}
