package com.thoughtbot.expandablerecyclerview;

import com.thoughtbot.expandablerecyclerview.listeners.ExpandCollapseListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableList;
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition;

/**
 * 此类位于支持 {@link ExpandableList} 和 {@link ExpandableRecyclerViewAdapter} 之间，并调解 {@link ExpandableGroup} 的展开和折叠
 */
public class ExpandCollapseController {

  private ExpandCollapseListener listener;
  private ExpandableList expandableList;

  public ExpandCollapseController(ExpandableList expandableList, ExpandCollapseListener listener) {
    this.expandableList = expandableList;
    this.listener = listener;
  }

  /**
   * 收起群组
   *
   * @param listPosition 要折叠的组的位置
   */
  private void collapseGroup(ExpandableListPosition listPosition) {
    expandableList.expandedGroupIndexes[listPosition.groupPos] = false;
    if (listener != null) {
      listener.onGroupCollapsed(expandableList.getFlattenedGroupIndex(listPosition) + 1,
          expandableList.groups.get(listPosition.groupPos).getItemCount());
    }
  }

  /**
   * 展开群组
   *
   * @param listPosition 要展开的组
   */
  private void expandGroup(ExpandableListPosition listPosition) {
    expandableList.expandedGroupIndexes[listPosition.groupPos] = true;
    if (listener != null) {
      listener.onGroupExpanded(expandableList.getFlattenedGroupIndex(listPosition) + 1,
          expandableList.groups.get(listPosition.groupPos).getItemCount());
    }
  }

  /**
   * @param group 正在检查其折叠状态的 {@link ExpandableGroup}
   * @return 如果 {@code group} 展开为真，如果折叠为假
   */
  public boolean isGroupExpanded(ExpandableGroup group) {
    int groupIndex = expandableList.groups.indexOf(group);
    return expandableList.expandedGroupIndexes[groupIndex];
  }

  /**
   * @param flatPos 列表中项目的展平位置
   * @return 如果 {@code group} 展开为真，如果折叠为假
   */
  public boolean isGroupExpanded(int flatPos) {
    ExpandableListPosition listPosition = expandableList.getUnflattenedPosition(flatPos);
    return expandableList.expandedGroupIndexes[listPosition.groupPos];
  }

  /**
   * @param flatPos 组的平面列表位置
   * @return false 如果组被展开，*after* 切换，如果组现在折叠，则为 true
   */
  public boolean toggleGroup(int flatPos) {
    ExpandableListPosition listPos = expandableList.getUnflattenedPosition(flatPos);
    boolean expanded = expandableList.expandedGroupIndexes[listPos.groupPos];
    if (expanded) {
      collapseGroup(listPos);
    } else {
      expandGroup(listPos);
    }
    return expanded;
  }

  public boolean toggleGroup(ExpandableGroup group) {
    ExpandableListPosition listPos =
        expandableList.getUnflattenedPosition(expandableList.getFlattenedGroupIndex(group));
    boolean expanded = expandableList.expandedGroupIndexes[listPos.groupPos];
    if (expanded) {
      collapseGroup(listPos);
    } else {
      expandGroup(listPos);
    }
    return expanded;
  }

  /**
   * @param group 正在扩展的 {@link ExpandableGroup}
   */
  void expandGroup(ExpandableGroup group) {
    ExpandableListPosition listPos =
            expandableList.getUnflattenedPosition(expandableList.getFlattenedGroupIndex(group));
    boolean isExpanded = expandableList.expandedGroupIndexes[listPos.groupPos];
    // No-op on repeating calls
    if (!isExpanded) {
      expandGroup(listPos);
    }
  }

  /**
   * @param flatPos 组的平面列表位置
   */
  void expandGroup(int flatPos) {
    ExpandableListPosition listPos = expandableList.getUnflattenedPosition(flatPos);
    boolean isExpanded = expandableList.expandedGroupIndexes[listPos.groupPos];
    // No-op on repeating calls
    if (!isExpanded) {
      expandGroup(listPos);
    }
  }

  /**
   * @param group 正在折叠的 {@link ExpandableGroup}
   */
  void collapseGroup(ExpandableGroup group) {
    ExpandableListPosition listPos =
            expandableList.getUnflattenedPosition(expandableList.getFlattenedGroupIndex(group));
    boolean isExpanded = expandableList.expandedGroupIndexes[listPos.groupPos];
    // No-op on repeating calls
    if (isExpanded) {
      collapseGroup(listPos);
    }
  }

  /**
   * @param flatPos 组的平面列表位置
   */
  void collapseGroup(int flatPos) {
    ExpandableListPosition listPos = expandableList.getUnflattenedPosition(flatPos);
    boolean isExpanded = expandableList.expandedGroupIndexes[listPos.groupPos];
    // No-op on repeating calls
    if (isExpanded) {
      collapseGroup(listPos);
    }
  }
}