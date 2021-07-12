package com.thoughtbot.expandablecheckrecyclerview.listeners;

import com.thoughtbot.expandablecheckrecyclerview.models.CheckedExpandableGroup;

public interface OnChildrenCheckStateChangedListener {

  /**
   * @param firstChildFlattenedIndex {@link CheckedExpandableGroup} 中第一个孩子的平面位置
   * @param numChildren {@link CheckedExpandableGroup} 中的孩子总数
   */
  void updateChildrenCheckState(int firstChildFlattenedIndex, int numChildren);
}
