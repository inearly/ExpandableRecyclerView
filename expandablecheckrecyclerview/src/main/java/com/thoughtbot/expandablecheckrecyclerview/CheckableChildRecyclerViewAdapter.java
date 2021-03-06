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
     * ?????????????????????????????????????????????
     * <p>
     * ??????????????? {@link CheckableChildRecyclerViewAdapter} ???????????? RecyclerView ????????? {@link Activity} ?????????
     * <p>
     * ??????????????????????????????????????????????????????????????? {@link #onRestoreInstanceState(Bundle)} ?????????????????????????????????
     *
     * @param outState {@code Bundle} ????????????????????????????????????
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(CHECKED_STATE_MAP, new ArrayList(expandableList.groups));
        super.onSaveInstanceState(outState);
    }

    /**
     * ???????????????????????? {@link Bundle} ????????????????????????????????????????????????????????????????????????
     * <p>
     * ?????? {@link Activity} ?????? {@link Activity#onRestoreInstanceState(Bundle)} ?????????
     * ??? {@link Activity} ????????? {@link CheckableChildRecyclerViewAdapter} ???????????? RecyclerView???
     * <p>
     *
     * @param savedInstanceState ????????????????????????????????? {@code Bundle}
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
     * ??????????????????????????????????????????????????????
     *
     * @param ??????????????????????????????true ?????????????????????false ???????????????????????????
     * @param groupIndex     {@code ExpandableGroup} ??? {@code getGroups()} ????????????
     * @param childIndex     ????????????????????????
     */
    public void checkChild(boolean checked, int groupIndex, int childIndex) {
        childCheckController.checkChild(checked, groupIndex, childIndex);
        if (childClickListener != null) {
            childClickListener.onCheckChildCLick(null, checked,
                    (CheckedExpandableGroup) expandableList.groups.get(groupIndex), childIndex);
        }
    }

    /**
     * ?????????????????????????????????
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
     * ???????????????????????????????????? #onCreateViewHolder(ViewGroup, int) ??????
     *
     * @param parent ?????????????????? {@link CCVH} ??????????????? {@link ViewGroup}
     * @return A {@link CCVH} ??????????????? {@code ViewGroup} ?????????????????????
     */
    public abstract CCVH onCreateCheckChildViewHolder(ViewGroup parent, int viewType);

    /**
     * ?????????????????????????????????????????? onBindViewHolder(RecyclerView.ViewHolder, int) ?????????
     * <p>
     * ??????????????????????????? {@link CCVH}???
     *
     * @param holder       ????????????????????? {@code CCVH}
     * @param flatPosition ??????????????????????????????????????????????????????
     * @param group        ????????????????????? {@link CheckedExpandableGroup}
     * @param childIndex   ????????????????????? {@link CheckedExpandableGroup} ????????????
     */
    public abstract void onBindCheckChildViewHolder(CCVH holder, int flatPosition, CheckedExpandableGroup group, int childIndex);
}
