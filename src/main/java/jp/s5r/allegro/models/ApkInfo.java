package jp.s5r.allegro.models;

import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

import java.net.URI;
import java.util.Date;

@JsonModel(decamelize = true)
public class ApkInfo {
  @JsonKey
  private String title;

  @JsonKey
  private String uri;

//  @JsonKey
  private Date lastModified;

  @JsonKey
  private long size;

  public ApkInfo() {
  }

  public ApkInfo(final String title,
                 final String uri,
                 final Date lastModified,
                 final long size) {
    setTitle(title);
    setUri(uri);
    setLastModified(lastModified);
    setSize(size);
  }

  @Deprecated
  public ApkInfo(String title, URI uri, long fileSize, Date lastModified) {
    setTitle(title);
    setUri(uri.toString());
    setSize(fileSize);
    setLastModified(lastModified);
  }

  public String getTitle() {
    if (title == null) {
      return "";
    }
    return title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(final String uri) {
    this.uri = uri;
  }

  public Date getLastModified() {
    if (lastModified == null) {
      return new Date();
    }
    return lastModified;
  }

  public void setLastModified(final Date lastModified) {
    this.lastModified = lastModified;
  }

  public long getSize() {
    return size;
  }

  public void setSize(final long size) {
    this.size = size;
  }
}
