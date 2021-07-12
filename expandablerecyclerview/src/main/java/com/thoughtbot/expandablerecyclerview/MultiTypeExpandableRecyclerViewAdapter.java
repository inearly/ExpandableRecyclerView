package com.thoughtbot.expandablerecyclerview;

//import android.support.v7.widget.RecyclerView.ViewHolder;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableList;
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

public abstract class MultiTypeExpandableRecyclerViewAdapter<GVH extends GroupViewHolder, CVH extends ChildViewHolder>
        extends ExpandableRecyclerViewAdapter<GVH, CVH> {

    public MultiTypeExpandableRecyclerViewAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    /**
     * RecyclerView.Adapter.onCreateViewHolder(ViewGroup, int) 的实现，它确定列表项是组还是子项，并调用 {@link #onCreateGroupViewHolder(ViewGroup, int)}
     * 或 {@link #onCreateChildViewHolder 的适当实现 (ViewGroup, int)}}。
     *
     * @param parent   新的 {@link android.view.View} 绑定到一个适配器位置后将添加到其中的 {@link ViewGroup}。
     * @param viewType 新{@code android.view.View} 的视图类型。
     * @return 包含给定视图类型的 {@code android.view.View} 的新 {@link GroupViewHolder} 或新 {@link ChildViewHolder}。
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isGroup(viewType)) {
            GVH gvh = onCreateGroupViewHolder(parent, viewType);
            gvh.setOnGroupClickListener(this);
            return gvh;
        } else if (isChild(viewType)) {
            CVH cvh = onCreateChildViewHolder(parent, viewType);
            return cvh;
        }
        throw new IllegalArgumentException("viewType is not valid");
    }

    /**
     * Adapter.onBindViewHolder(RecyclerView.ViewHolder, int) 的实现，它确定列表项是组还是子项，
     * 并调用 {@link #onBindGroupViewHolder(GroupViewHolder, int, ExpandableGroup)} 或 {@link #onBindChildViewHolder(ChildViewHolder, int, ExpandableGroup, int)}。
     *
     * @param holder   要绑定数据的 GroupViewHolder 或 ChildViewHolder
     * @param position 要绑定的列表中的平面位置（或{@link ExpandableList#getVisibleItemCount()} 列表中的索引
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ExpandableListPosition listPos = expandableList.getUnflattenedPosition(position);
        ExpandableGroup group = expandableList.getExpandableGroup(listPos);
        if (isGroup(getItemViewType(position))) {
            onBindGroupViewHolder((GVH) holder, position, group);

            if (isGroupExpanded(group)) {
                ((GVH) holder).expand();
            } else {
                ((GVH) holder).collapse();
            }
        } else if (isChild(getItemViewType(position))) {
            onBindChildViewHolder((CVH) holder, position, group, listPos.childPos);
        }
    }

    /**
     * 获取给定位置项目的视图类型。
     *
     * @param position 列表中要获取的视图类型的平面位置
     * @return 如果平面位置对应一个子项，这将返回 {@code getChildViewType} 返回的值。 如果平面位置指的是一个组项目，
     * 这将返回 {@code getGroupViewType} 返回的值
     */
    @Override
    public int getItemViewType(int position) {
        ExpandableListPosition listPosition = expandableList.getUnflattenedPosition(position);
        ExpandableGroup group = expandableList.getExpandableGroup(listPosition);

        int viewType = listPosition.type;
        switch (viewType) {
            case ExpandableListPosition.GROUP:
                return getGroupViewType(position, group);
            case ExpandableListPosition.CHILD:
                return getChildViewType(position, group, listPosition.childPos);
            default:
                return viewType;
        }
    }

    /**
     * 用于允许子类为子类拥有多种视图类型
     *
     * @param position   列表中的平面位置
     * @param group      这个孩子所属的组
     * @param childIndex 组内孩子的索引
     * @return 任何代表 {@code group} *EXCEPT* 中的孩子的 viewType 的 int {@link ExpandableListPosition#CHILD} 和 {@link ExpandableListPosition#GROUP}。
     * <p>
     * 如果您*不*覆盖此方法，则组的默认视图类型为 {@link ExpandableListPosition#CHILD}
     *
     * <p>
     * 子类可以为 {@link ExpandableListPosition#CHILD} 和 {@link ExpandableListPosition#GROUP} 使用任意数量的 *EXCEPT*，因为它们已被适配器使用
     * </p>
     */
    public int getChildViewType(int position, ExpandableGroup group, int childIndex) {
        return super.getItemViewType(position);
    }

    /**
     * 用于允许子类具有多个组的视图类型
     *
     * @param position 列表中的平面位置
     * @param group    这个位置的组
     * @return 表示此 {@code group} 的 viewType 的任何 int *EXCEPT* 用于 {@link ExpandableListPosition#CHILD} 和 {@link ExpandableListPosition#GROUP}。
     * <p>
     * 如果您不覆盖此方法，则组的默认视图类型为 {@link ExpandableListPosition#GROUP}
     *
     * <p>
     * 子类可以为 {@link ExpandableListPosition#CHILD} 和 {@link ExpandableListPosition#GROUP} 使用任意数量的 *EXCEPT*，因为它们已被适配器使用
     * </p>
     */
    public int getGroupViewType(int position, ExpandableGroup group) {
        return super.getItemViewType(position);
    }

    /**
     * @param viewType 对应于 {@code ExpandableGroup} 的 viewType 的 int
     * @return 如果子类 *NOT* 覆盖 {@code getGroupViewType} 而不是该组的 viewType 默认为 {@link ExpandableListPosition#GROUP}
     */
    public boolean isGroup(int viewType) {
        return viewType == ExpandableListPosition.GROUP;
    }

    /**
     * @param viewType 对应于 {@code ExpandableGroup} 的孩子的 viewType 的 int
     * @return 如果子类 *NOT* 覆盖 {@code getChildViewType} 而不是子类的 viewType 默认为 {@link ExpandableListPosition#CHILD}
     */
    public boolean isChild(int viewType) {
        return viewType == ExpandableListPosition.CHILD;
    }
}
