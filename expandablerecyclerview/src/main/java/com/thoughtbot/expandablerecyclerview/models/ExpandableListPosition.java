package com.thoughtbot.expandablerecyclerview.models;

import android.widget.ExpandableListView;

import java.util.ArrayList;

/**
 * android.widget.ExpandableListPosition 的精确副本，因为 android.widget.ExpandableListPosition 具有包本地范围
 * <p>
 * ExpandableListPosition 可以指组的位置或子项的位置。 提及孩子的位置需要组位置（包含孩子的组）和孩子位置（孩子在该组中的位置）。
 * 要创建对象，请使用 {@link #obtainChildPosition(int, int)} 或 {@link #obtainGroupPosition(int)}。
 */

public class ExpandableListPosition {

    private static final int MAX_POOL_SIZE = 5;
    private static ArrayList<ExpandableListPosition> sPool =
            new ArrayList<ExpandableListPosition>(MAX_POOL_SIZE);

    /**
     * 此数据类型表示子位置
     */
    public final static int CHILD = 1;

    /**
     * 此数据类型代表组位置
     */
    public final static int GROUP = 2;

    /**
     * 被引用组的位置，或被引用子组的父组
     */
    public int groupPos;

    /**
     * 孩子在其父组中的位置
     */
    public int childPos;

    /**
     * 项目在平面列表中的位置（可选，当组或子项的相应平面列表位置已知时在内部使用）
     */
    int flatListPos;

    /**
     * 这个 ExpandableListPosition 代表什么类型的位置
     */
    public int type;

    private void resetState() {
        groupPos = 0;
        childPos = 0;
        flatListPos = 0;
        type = 0;
    }

    private ExpandableListPosition() {
    }

    public long getPackedPosition() {
        if (type == CHILD) {
            return ExpandableListView.getPackedPositionForChild(groupPos, childPos);
        } else {
            return ExpandableListView.getPackedPositionForGroup(groupPos);
        }
    }

    static ExpandableListPosition obtainGroupPosition(int groupPosition) {
        return obtain(GROUP, groupPosition, 0, 0);
    }

    static ExpandableListPosition obtainChildPosition(int groupPosition, int childPosition) {
        return obtain(CHILD, groupPosition, childPosition, 0);
    }

    static ExpandableListPosition obtainPosition(long packedPosition) {
        if (packedPosition == ExpandableListView.PACKED_POSITION_VALUE_NULL) {
            return null;
        }

        ExpandableListPosition elp = getRecycledOrCreate();
        elp.groupPos = ExpandableListView.getPackedPositionGroup(packedPosition);
        if (ExpandableListView.getPackedPositionType(packedPosition) ==
                ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            elp.type = CHILD;
            elp.childPos = ExpandableListView.getPackedPositionChild(packedPosition);
        } else {
            elp.type = GROUP;
        }
        return elp;
    }

    public static ExpandableListPosition obtain(int type, int groupPos, int childPos,
                                                int flatListPos) {
        ExpandableListPosition elp = getRecycledOrCreate();
        elp.type = type;
        elp.groupPos = groupPos;
        elp.childPos = childPos;
        elp.flatListPos = flatListPos;
        return elp;
    }

    private static ExpandableListPosition getRecycledOrCreate() {
        ExpandableListPosition elp;
        synchronized (sPool) {
            if (sPool.size() > 0) {
                elp = sPool.remove(0);
            } else {
                return new ExpandableListPosition();
            }
        }
        elp.resetState();
        return elp;
    }

    /**
     * 除非您通过 ExpandableListPosition.obtain() 获得它，否则不要调用它。 PositionMetadata 将处理回收它自己的孩子。
     */
    public void recycle() {
        synchronized (sPool) {
            if (sPool.size() < MAX_POOL_SIZE) {
                sPool.add(this);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpandableListPosition that = (ExpandableListPosition) o;

        if (groupPos != that.groupPos) return false;
        if (childPos != that.childPos) return false;
        if (flatListPos != that.flatListPos) return false;
        return type == that.type;

    }

    @Override
    public int hashCode() {
        int result = groupPos;
        result = 31 * result + childPos;
        result = 31 * result + flatListPos;
        result = 31 * result + type;
        return result;
    }

    @Override
    public String toString() {
        return "ExpandableListPosition{" +
                "groupPos=" + groupPos +
                ", childPos=" + childPos +
                ", flatListPos=" + flatListPos +
                ", type=" + type +
                '}';
    }
}

