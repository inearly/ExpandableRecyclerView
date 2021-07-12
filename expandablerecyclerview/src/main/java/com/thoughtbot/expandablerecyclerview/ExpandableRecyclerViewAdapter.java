package com.thoughtbot.expandablerecyclerview;

import android.app.Activity;
import android.os.Bundle;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.thoughtbot.expandablerecyclerview.listeners.ExpandCollapseListener;
import com.thoughtbot.expandablerecyclerview.listeners.GroupExpandCollapseListener;
import com.thoughtbot.expandablerecyclerview.listeners.OnGroupClickListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableList;
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

public abstract class ExpandableRecyclerViewAdapter<GVH extends GroupViewHolder, CVH extends ChildViewHolder>
        extends RecyclerView.Adapter implements ExpandCollapseListener, OnGroupClickListener {

    private static final String EXPAND_STATE_MAP = "expandable_recyclerview_adapter_expand_state_map";

    protected ExpandableList expandableList;
    private ExpandCollapseController expandCollapseController;

    private OnGroupClickListener groupClickListener;
    private GroupExpandCollapseListener expandCollapseListener;

    public ExpandableRecyclerViewAdapter(List<? extends ExpandableGroup> groups) {
        this.expandableList = new ExpandableList(groups);
        this.expandCollapseController = new ExpandCollapseController(expandableList, this);
    }

    /**
     * Adapter.onCreateViewHolder(ViewGroup, int) 的实现，它确定列表项是组还是子项并通过调用
     * <p>
     * 到 {@link #onCreateGroupViewHolder(ViewGroup, int)} 或 {@link #onCreateChildViewHolder(ViewGroup, int)}} 的适当实现。
     *
     * @param parent   新的 {@link android.view.View} 绑定到一个适配器位置后将添加到其中的 {@link ViewGroup}。
     * @param viewType 新{@code android.view.View} 的视图类型。
     * @return 包含给定视图类型的 {@code android.view.View} 的新 {@link GroupViewHolder} 或新 {@link ChildViewHolder}。
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ExpandableListPosition.GROUP:
                GVH gvh = onCreateGroupViewHolder(parent, viewType);
                gvh.setOnGroupClickListener(this);
                return gvh;
            case ExpandableListPosition.CHILD:
                CVH cvh = onCreateChildViewHolder(parent, viewType);
                return cvh;
            default:
                throw new IllegalArgumentException("viewType is not valid");
        }
    }

    /**
     * Adapter.onBindViewHolder(RecyclerView.ViewHolder, int) 的实现，它确定列表项是组还是子项，
     * 并调用 {@link #onBindGroupViewHolder(GroupViewHolder, int, ExpandableGroup)}
     * 或 {@link #onBindChildViewHolder(ChildViewHolder, int, ExpandableGroup, int)}。
     *
     * @param holder   要绑定数据的 GroupViewHolder 或 ChildViewHolder
     * @param position 要绑定的列表中的平面位置（或{@link ExpandableList#getVisibleItemCount()} 列表中的索引
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ExpandableListPosition listPos = expandableList.getUnflattenedPosition(position);
        ExpandableGroup group = expandableList.getExpandableGroup(listPos);
        switch (listPos.type) {
            case ExpandableListPosition.GROUP:
                onBindGroupViewHolder((GVH) holder, position, group);

                if (isGroupExpanded(group)) {
                    ((GVH) holder).expand();
                } else {
                    ((GVH) holder).collapse();
                }
                break;
            case ExpandableListPosition.CHILD:
                onBindChildViewHolder((CVH) holder, position, group, listPos.childPos);
                break;
        }
    }

    /**
     * @return 当前展开的组和子对象的数量
     * @see ExpandableList#getVisibleItemCount()
     */
    @Override
    public int getItemCount() {
        return expandableList.getVisibleItemCount();
    }

    /**
     * 获取给定位置项目的视图类型。
     *
     * @param position 列表中要获取的视图类型的平面位置
     * @return {@value ExpandableListPosition#CHILD} 或 {@value ExpandableListPosition#GROUP}
     * @throws RuntimeException 如果未找到列表中给定位置的项目
     */
    @Override
    public int getItemViewType(int position) {
        return expandableList.getUnflattenedPosition(position).type;
    }

    /**
     * 组展开时调用
     *
     * @param positionStart {@link ExpandableGroup} 中第一个孩子的平面位置
     * @param itemCount     {@link ExpandableGroup} 中子项的总数
     */
    @Override
    public void onGroupExpanded(int positionStart, int itemCount) {
        //update header
        int headerPosition = positionStart - 1;
        notifyItemChanged(headerPosition);

        // only insert if there items to insert
        if (itemCount > 0) {
            notifyItemRangeInserted(positionStart, itemCount);
            if (expandCollapseListener != null) {
                int groupIndex = expandableList.getUnflattenedPosition(positionStart).groupPos;
                expandCollapseListener.onGroupExpanded(getGroups().get(groupIndex));
            }
        }
    }

    /**
     * 当组折叠时调用
     *
     * @param positionStart {@link ExpandableGroup} 中第一个孩子的平面位置
     * @param itemCount     {@link ExpandableGroup} 中子项的总数
     */
    @Override
    public void onGroupCollapsed(int positionStart, int itemCount) {
        //update header
        int headerPosition = positionStart - 1;
        notifyItemChanged(headerPosition);

        // only remote if there items to remove
        if (itemCount > 0) {
            notifyItemRangeRemoved(positionStart, itemCount);
            if (expandCollapseListener != null) {
                //minus one to return the position of the header, not first child
                int groupIndex = expandableList.getUnflattenedPosition(positionStart - 1).groupPos;
                expandCollapseListener.onGroupCollapsed(getGroups().get(groupIndex));
            }
        }
    }

    /**
     * 由点击 {@link GroupViewHolder} 触发
     *
     * @param flatPos 被点击的 {@link GroupViewHolder} 的平面位置
     * @return 如果单击展开组为 false，则单击折叠组为 true
     */
    @Override
    public boolean onGroupClick(int flatPos) {
        if (groupClickListener != null) {
            groupClickListener.onGroupClick(flatPos);
        }
        return expandCollapseController.toggleGroup(flatPos);
    }

    /**
     * @param flatPos 组的平面列表位置
     * @return 如果组已展开，则为 true，*after* 切换，如果组现在已折叠，则为 false
     */
    public boolean toggleGroup(int flatPos) {
        return expandCollapseController.toggleGroup(flatPos);
    }

    /**
     * @param group 正在切换的 {@link ExpandableGroup}
     * @return 如果组已展开，则为 true，*after* 切换，如果组现在已折叠，则为 false
     */
    public boolean toggleGroup(ExpandableGroup group) {
        return expandCollapseController.toggleGroup(group);
    }

    /**
     * 显式扩展一个组。 扩展已经扩展的组没有任何作用。
     *
     * @param group 正在扩展的 {@link ExpandableGroup}
     */
    public void expandGroup(ExpandableGroup group) {
        expandCollapseController.expandGroup(group);
    }

    /**
     * 显式折叠组。 折叠已经折叠的组没有任何作用。
     *
     * @param group 正在扩展的 {@link ExpandableGroup}
     */
    public void collapseGroup(ExpandableGroup group) {
        expandCollapseController.collapseGroup(group);
    }

    /**
     * @param flatPos 列表中项目的展平位置
     * @return 如果 {@code group} 展开为真，如果折叠为假
     */
    public boolean isGroupExpanded(int flatPos) {
        return expandCollapseController.isGroupExpanded(flatPos);
    }

    /**
     * @param group 正在检查其折叠状态的 {@link ExpandableGroup}
     * @return 如果 {@code group} 展开为真，如果折叠为假
     */
    public boolean isGroupExpanded(ExpandableGroup group) {
        return expandCollapseController.isGroupExpanded(group);
    }

    /**
     * 跨状态损失存储扩展的状态图。
     * <p>
     * 应该从承载 {@link ExpandableRecyclerViewAdapter} 附加到的 RecyclerView 的任何 {@link Activity} 调用。
     * <p>
     * 这将确保将扩展状态映射作为额外添加到要在 {@link #onRestoreInstanceState(Bundle)} 中使用的实例状态包中。
     *
     * @param savedInstanceState 用于存储扩展状态映射的 {@code Bundle}
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBooleanArray(EXPAND_STATE_MAP, expandableList.expandedGroupIndexes);
    }

    /**
     * 从保存的实例状态 {@link Bundle} 中获取可扩展状态映射并恢复所有列表项的扩展状态。
     * <p>
     * 应该从承载 RecyclerView 的 {@link Activity} 中的 {@link Activity#onRestoreInstanceState(Bundle)} 调用
     * {@link ExpandableRecyclerViewAdapter} 附加到。
     * <p>
     *
     * @param savedInstanceState 从中加载扩展状态映射的 {@code Bundle}
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null || !savedInstanceState.containsKey(EXPAND_STATE_MAP)) {
            return;
        }
        expandableList.expandedGroupIndexes = savedInstanceState.getBooleanArray(EXPAND_STATE_MAP);
        notifyDataSetChanged();
    }

    public void setOnGroupClickListener(OnGroupClickListener listener) {
        groupClickListener = listener;
    }

    public void setOnGroupExpandCollapseListener(GroupExpandCollapseListener listener) {
        expandCollapseListener = listener;
    }

    /**
     * 支持此 RecyclerView 的 {@link ExpandableGroup} 的完整列表
     *
     * @return 实例化此对象所用的 {@link ExpandableGroup} 列表
     */
    public List<? extends ExpandableGroup> getGroups() {
        return expandableList.groups;
    }

    /**
     * 当创建的列表项是一个组时从 {@link #onCreateViewHolder(ViewGroup, int)} 调用
     *
     * @param viewType 由 {@link ExpandableRecyclerViewAdapter#getItemViewType(int)} 返回的 int
     * @param parent   正在为其创建 {@link GVH} 的列表中的 {@link ViewGroup}
     * @return 一个 {@link GVH} 对应于具有 {@code ViewGroup} 父级的组列表项
     */
    public abstract GVH onCreateGroupViewHolder(ViewGroup parent, int viewType);

    /**
     * 当创建的列表项是子项时从 {@link #onCreateViewHolder(ViewGroup, int)} 调用
     *
     * @param viewType 由 {@link ExpandableRecyclerViewAdapter#getItemViewType(int)} 返回的 int
     * @param parent   正在为其创建 {@link CVH} 的列表中的 {@link ViewGroup}
     * @return A {@link CVH} 对应于具有 {@code ViewGroup} 父级的子列表项
     */
    public abstract CVH onCreateChildViewHolder(ViewGroup parent, int viewType);

    /**
     * 当绑定到的列表项是子项时，从 onBindViewHolder(RecyclerView.ViewHolder, int) 调用。
     * <p>
     * 在此处将数据绑定到 {@link CVH}。
     *
     * @param holder       将数据绑定到的 {@code CVH}
     * @param flatPosition 列表中绑定子项的平面位置（原始索引）
     * @param group        子列表项所属的{@link ExpandableGroup}
     * @param childIndex   这个孩子在它的 {@link ExpandableGroup} 中的索引
     */
    public abstract void onBindChildViewHolder(CVH holder, int flatPosition, ExpandableGroup group,
                                               int childIndex);

    /**
     * 当绑定的列表项是一个组时从 onBindViewHolder(RecyclerView.ViewHolder, int) 调用
     * <p>
     * 在此处将数据绑定到 {@link GVH}。
     *
     * @param holder       将数据绑定到的 {@code GVH}
     * @param flatPosition 列表中绑定组的平面位置（原始索引）
     * @param group        用于将数据绑定到此 {@link GVH} 的 {@link ExpandableGroup}
     */
    public abstract void onBindGroupViewHolder(GVH holder, int flatPosition, ExpandableGroup group);
}
