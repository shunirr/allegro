package jp.s5r.allegro.models;

import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

import android.net.Uri;

import java.util.Date;

@JsonModel(decamelize = true)
public class ApkInfo {
  @JsonKey
  private String title;

  @JsonKey
  private String uri;

  @JsonKey
  private Date lastModified;

  @JsonKey
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
    return Uri.parse(uri);
  }

  public void setUri(final Uri uri) {
    this.uri = uri.toString();
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
