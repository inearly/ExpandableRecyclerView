package com.thoughtbot.expandablerecyclerview.listeners;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

public interface GroupExpandCollapseListener {

  /**
   * 组展开时调用
   * @param group 正在扩展的 {@link ExpandableGroup}
   */
  void onGroupExpanded(ExpandableGroup group);

  /**
   * 当组折叠时调用
   * @param group 正在折叠的 {@link ExpandableGroup}
   */
  void onGroupCollapsed(ExpandableGroup group);
}
