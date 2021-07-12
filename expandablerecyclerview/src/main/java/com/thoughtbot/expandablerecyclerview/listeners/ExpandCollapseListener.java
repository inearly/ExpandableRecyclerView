package com.thoughtbot.expandablerecyclerview.listeners;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

public interface ExpandCollapseListener {

  /**
   * 组展开时调用
   * @param positionStart {@link ExpandableGroup} 中第一个孩子的平面位置
   * @param itemCount {@link ExpandableGroup} 中子项的总数
   */
  void onGroupExpanded(int positionStart, int itemCount);

  /**
   * 当组折叠时调用
   * @param positionStart {@link ExpandableGroup} 中第一个孩子的平面位置
   * @param itemCount {@link ExpandableGroup} 中子项的总数
   */
  void onGroupCollapsed(int positionStart, int itemCount);
}
