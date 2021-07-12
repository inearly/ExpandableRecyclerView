package com.thoughtbot.expandablecheckrecyclerview;

import android.widget.Checkable;
import android.widget.ExpandableListView;

import com.thoughtbot.expandablecheckrecyclerview.listeners.OnChildrenCheckStateChangedListener;
import com.thoughtbot.expandablecheckrecyclerview.models.CheckedExpandableGroup;
import com.thoughtbot.expandablecheckrecyclerview.viewholders.CheckableChildViewHolder;
import com.thoughtbot.expandablerecyclerview.models.ExpandableList;
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition;

import java.util.ArrayList;
import java.util.List;

public class ChildCheckController {

    private ExpandableList expandableList;
    private OnChildrenCheckStateChangedListener childrenUpdateListener;
    private List<Integer> initialCheckedPositions;

    public ChildCheckController(ExpandableList expandableList,
                                OnChildrenCheckStateChangedListener listener) {
        this.expandableList = expandableList;
        this.childrenUpdateListener = listener;
        initialCheckedPositions = getCheckedPositions();
    }

    /**
     * 由 {@link CheckableChildViewHolder} 上的点击事件触发，导致 {@link Checkable} 对象更改选中状态
     *
     * @param checked      视图的当前选中状态
     * @param listPosition {@link CheckableChildViewHolder} 的平面位置（原始索引）
     */
    public void onChildCheckChanged(boolean checked, ExpandableListPosition listPosition) {
        CheckedExpandableGroup group =
                (CheckedExpandableGroup) expandableList.groups.get(listPosition.groupPos);
        group.onChildClicked(listPosition.childPos, checked);
        if (childrenUpdateListener != null) {
            childrenUpdateListener.updateChildrenCheckState(
                    expandableList.getFlattenedFirstChildIndex(listPosition),
                    expandableList.getExpandableGroupItemCount(listPosition));
        }
    }

    public void checkChild(boolean checked, int groupIndex, int childIndex) {
        CheckedExpandableGroup group = (CheckedExpandableGroup) expandableList.groups.get(groupIndex);
        group.onChildClicked(childIndex, checked);
        if (childrenUpdateListener != null) {
            //only update children check states if group is expanded
            boolean isGroupExpanded = expandableList.expandedGroupIndexes[groupIndex];
            if (isGroupExpanded) {
                childrenUpdateListener.updateChildrenCheckState(
                        expandableList.getFlattenedFirstChildIndex(groupIndex), group.getItemCount());
            }
        }
    }

    /**
     * @param listPosition 子列表项的 ExpandableListPosition 表示
     * @return 视图的当前选中状态
     */
    public boolean isChildChecked(ExpandableListPosition listPosition) {
        CheckedExpandableGroup group =
                (CheckedExpandableGroup) expandableList.groups.get(listPosition.groupPos);
        return group.isChildChecked(listPosition.childPos);
    }

    /**
     * @return 所有选中子项的索引列表
     */
    public List<Integer> getCheckedPositions() {
        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < expandableList.groups.size(); i++) {
            if (expandableList.groups.get(i) instanceof CheckedExpandableGroup) {
                CheckedExpandableGroup group = (CheckedExpandableGroup) expandableList.groups.get(i);
                for (int j = 0; j < group.getItemCount(); j++) {
                    if (group.isChildChecked(j)) {
                        long packedPosition = ExpandableListView.getPackedPositionForChild(i, j);
                        selected.add(expandableList.getFlattenedChildIndex(packedPosition));
                    }
                }
            }
        }
        return selected;
    }

    /**
     * @return true 如果任何子项的检查状态自此类初始化以来已更改
     */
    public boolean checksChanged() {
        return !initialCheckedPositions.equals(getCheckedPositions());
    }

    /**
     * 清除之前选中的所有选项
     */
    public void clearCheckStates() {
        for (int i = 0; i < expandableList.groups.size(); i++) {
            CheckedExpandableGroup group = (CheckedExpandableGroup) expandableList.groups.get(i);
            group.clearSelections();
        }
    }
}
