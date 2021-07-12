package com.thoughtbot.expandablecheckrecyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablecheckrecyclerview.listeners.OnCheckChildClickListener;
import com.thoughtbot.expandablecheckrecyclerview.listeners.OnChildCheckChangedListener;
import com.thoughtbot.expandablecheckrecyclerview.listeners.OnChildrenCheckStateChangedListener;
import com.thoughtbot.expandablecheckrecyclerview.models.CheckedExpandableGroup;
import com.thoughtbot.expandablecheckrecyclerview.viewholders.CheckableChildViewHolder;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class CheckableChildRecyclerViewAdapter<GVH extends GroupViewHolder, CCVH extends CheckableChildViewHolder>
        extends ExpandableRecyclerViewAdapter<GVH, CCVH>
        implements OnChildCheckChangedListener, OnChildrenCheckStateChangedListener {

    private static final String CHECKED_STATE_MAP = "child_check_controller_checked_state_map";

    private ChildCheckController childCheckController;
    private OnCheckChildClickListener childClickListener;

    public CheckableChildRecyclerViewAdapter(List<? extends CheckedExpandableGroup> groups) {
        super(groups);
        childCheckController = new ChildCheckController(expandableList, this);
    }

    @Override
    public CCVH onCreateChildViewHolder(ViewGroup parent, int viewType) {
        CCVH CCVH = onCreateCheckChildViewHolder(parent, viewType);
        CCVH.setOnChildCheckedListener(this);
        return CCVH;
    }

    @Override
    public void onBindChildViewHolder(CCVH holder, int flatPosition, ExpandableGroup group,
                                      int childIndex) {
        ExpandableListPosition listPosition = expandableList.getUnflattenedPosition(flatPosition);
        holder.onBindViewHolder(flatPosition, childCheckController.isChildChecked(listPosition));
        onBindCheckChildViewHolder(holder, flatPosition, (CheckedExpandableGroup) group, childIndex);
    }

    @Override
    public void onChildCheckChanged(View view, boolean checked, int flatPos) {
        ExpandableListPosition listPos = expandableList.getUnflattenedPosition(flatPos);
        childCheckController.onChildCheckChanged(checked, listPos);
        if (childClickListener != null) {
            childClickListener.onCheckChildCLick(view, checked,
                    (CheckedExpandableGroup) expandableList.getExpandableGroup(listPos), listPos.childPos);
        }
    }

    @Override
    public void updateChildrenCheckState(int firstChildFlattenedIndex, int numChildren) {
        notifyItemRangeChanged(firstChildFlattenedIndex, numChildren);
    }

    public void setChildClickListener(OnCheckChildClickListener listener) {
        childClickListener = listener;
    }

    /**
     * 跨状态丢失存储已检查的状态图。
     * <p>
     * 应该从承载 {@link CheckableChildRecyclerViewAdapter} 附加到的 RecyclerView 的任何 {@link Activity} 调用。
     * <p>
     * 这将确保将选中的状态映射作为额外添加到要在 {@link #onRestoreInstanceState(Bundle)} 中使用的实例状态包中。
     *
     * @param outState {@code Bundle} 用于存储已检查的状态映射
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(CHECKED_STATE_MAP, new ArrayList(expandableList.groups));
        super.onSaveInstanceState(outState);
    }

    /**
     * 从保存的实例状态 {@link Bundle} 中获取选中状态映射并恢复所有子列表项的选中状态。
     * <p>
     * 应从 {@link Activity} 中的 {@link Activity#onRestoreInstanceState(Bundle)} 调用，
     * 该 {@link Activity} 承载此 {@link CheckableChildRecyclerViewAdapter} 附加到的 RecyclerView。
     * <p>
     *
     * @param savedInstanceState 从中加载扩展状态映射的 {@code Bundle}
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null || !savedInstanceState.containsKey(CHECKED_STATE_MAP)) {
            return;
        }
        expandableList.groups = savedInstanceState.getParcelableArrayList(CHECKED_STATE_MAP);
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 手动（以编程方式）更新子项的检查状态
     *
     * @param 检查所需的检查状态，true 将检查该项目，false 将尽可能取消选中它
     * @param groupIndex     {@code ExpandableGroup} 在 {@code getGroups()} 中的索引
     * @param childIndex     其组内孩子的索引
     */
    public void checkChild(boolean checked, int groupIndex, int childIndex) {
        childCheckController.checkChild(checked, groupIndex, childIndex);
        if (childClickListener != null) {
            childClickListener.onCheckChildCLick(null, checked,
                    (CheckedExpandableGroup) expandableList.groups.get(groupIndex), childIndex);
        }
    }

    /**
     * 清除之前选中的所有选项
     */
    public void clearChoices() {
        childCheckController.clearCheckStates();

        //only update the child views that are visible (i.e. their group is expanded)
        for (int i = 0; i < getGroups().size(); i++) {
            ExpandableGroup group = getGroups().get(i);
            if (isGroupExpanded(group)) {
                notifyItemRangeChanged(expandableList.getFlattenedFirstChildIndex(i), group.getItemCount());
            }
        }
    }

    /**
     * 当创建的列表项是子项时从 #onCreateViewHolder(ViewGroup, int) 调用
     *
     * @param parent 正在为其创建 {@link CCVH} 的列表中的 {@link ViewGroup}
     * @return A {@link CCVH} 对应于具有 {@code ViewGroup} 父级的子列表项
     */
    public abstract CCVH onCreateCheckChildViewHolder(ViewGroup parent, int viewType);

    /**
     * 当绑定到的列表项是子项时，从 onBindViewHolder(RecyclerView.ViewHolder, int) 调用。
     * <p>
     * 在此处将数据绑定到 {@link CCVH}。
     *
     * @param holder       将数据绑定到的 {@code CCVH}
     * @param flatPosition 列表中绑定子项的平面位置（原始索引）
     * @param group        子列表项所属的 {@link CheckedExpandableGroup}
     * @param childIndex   这个孩子在它的 {@link CheckedExpandableGroup} 中的索引
     */
    public abstract void onBindCheckChildViewHolder(CCVH holder, int flatPosition, CheckedExpandableGroup group, int childIndex);
}
