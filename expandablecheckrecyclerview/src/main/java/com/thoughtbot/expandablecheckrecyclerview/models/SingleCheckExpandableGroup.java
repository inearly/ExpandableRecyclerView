package com.thoughtbot.expandablecheckrecyclerview.models;

import android.os.Parcel;
import java.util.List;

/**
 * {@link CheckedExpandableGroup} 的一个子类，允许一次只检查*一个*孩子
 */
public class SingleCheckExpandableGroup extends CheckedExpandableGroup {

  public SingleCheckExpandableGroup(String title, List items) {
    super(title, items);
  }

  @Override
  public void onChildClicked(int childIndex, boolean checked) {
    if (checked) {
      for (int i = 0; i < getItemCount(); i++) {
        unCheckChild(i);
      }
      checkChild(childIndex);
    }
  }

  protected SingleCheckExpandableGroup(Parcel in) {
    super(in);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @SuppressWarnings("unused")
  public static final Creator<SingleCheckExpandableGroup> CREATOR =
      new Creator<SingleCheckExpandableGroup>() {
        @Override
        public SingleCheckExpandableGroup createFromParcel(Parcel in) {
          return new SingleCheckExpandableGroup(in);
        }

        @Override
        public SingleCheckExpandableGroup[] newArray(int size) {
          return new SingleCheckExpandableGroup[size];
        }
      };
}
