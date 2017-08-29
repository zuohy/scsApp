package cn.longmaster.doctorlibrary.util.photo;

import java.io.Serializable;

public class Photo implements Serializable {

  private int id;
  private String path;
  private boolean isSelect;

  public Photo(int id, String path) {
    this.id = id;
    this.path = path;
    this.isSelect = false;
  }

  public Photo() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Photo)) return false;

    Photo photo = (Photo) o;

    return id == photo.id;
  }

  @Override
  public int hashCode() {
    return id;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public boolean isSelect() {
    return isSelect;
  }

  public void setSelect(boolean select) {
    isSelect = select;
  }

  @Override
  public String toString() {
    return "Photo{" +
            "id=" + id +
            ", path='" + path + '\'' +
            ", isSelect=" + isSelect +
            '}';
  }
}