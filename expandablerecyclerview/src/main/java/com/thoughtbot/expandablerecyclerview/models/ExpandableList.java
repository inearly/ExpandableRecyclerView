package com.thoughtbot.expandablerecyclerview.models;

import java.util.List;

/*
 * 术语：
 * <li> flat position - 平面列表位置，一个项目相对于屏幕上所有其他*可见*项目的位置。 例如，如果你有一个三组，每组有 2 个孩子并且都折叠起来，最后一组的“平面位置”将为 2。如果这三个组中的第一个被展开，最后一组的平面位置 现在是 4。
 *
 * 该类充当平面列表位置之间的翻译器 - 即您在屏幕上看到的团体和儿童 - 与团体及其子女的完整支持名单之间的翻译
 */
public class ExpandableList {

    public List<? extends ExpandableGroup> groups;
    public boolean[] expandedGroupIndexes;

    public ExpandableList(List<? extends ExpandableGroup> groups) {
        this.groups = groups;

        expandedGroupIndexes = new boolean[groups.size()];
        for (int i = 0; i < groups.size(); i++) {
            expandedGroupIndexes[i] = false;
        }
    }

    /**
     * @param group {@link ExpandableGroup} 在完整集合中的索引 {@link #groups}
     * @return 特定组的可见行项目数。 如果组已折叠，则为组标题返回 1。 如果组被扩展，则返回组中孩子的总数 + 1 为组标题
     */
    private int numberOfVisibleItemsInGroup(int group) {
        if (expandedGroupIndexes[group]) {
            return groups.get(group).getItemCount() + 1;
        } else {
            return 1;
        }
    }

    /**
     * @return 总可见行数
     */
    public int getVisibleItemCount() {
        int count = 0;
        for (int i = 0; i < groups.size(); i++) {
            count += numberOfVisibleItemsInGroup(i);
        }
        return count;
    }

    /**
     * 将平面列表位置（列表中项目（子项或组）的原始位置）转换为 a) 组 pos 如果指定的平面列表位置对应于组，或 b)
     * 子 pos 如果它对应于子项。 如果是 exp 组，则在扩展组列表上执行二分搜索以查找平面列表 pos，否则查找平面列表 pos 在 exp 组之间适合的位置。
     *
     * @param flPos 要翻译的平面列表位置
     * @return 包含在 {@link ExpandableListPosition} 对象中的指定平面列表位置的组位置或子位置，该对象包含用于插入等的附加有用信息。
     */
    public ExpandableListPosition getUnflattenedPosition(int flPos) {
        int groupItemCount;
        int adapted = flPos;
        for (int i = 0; i < groups.size(); i++) {
            groupItemCount = numberOfVisibleItemsInGroup(i);
            if (adapted == 0) {
                return ExpandableListPosition.obtain(ExpandableListPosition.GROUP, i, -1, flPos);
            } else if (adapted < groupItemCount) {
                return ExpandableListPosition.obtain(ExpandableListPosition.CHILD, i, adapted - 1, flPos);
            }
            adapted -= groupItemCount;
        }
        throw new RuntimeException("Unknown state");
    }

    /**
     * @param listPosition 代表一个孩子或一个组
     * @return {@link #getVisibleItemCount()} 中组的索引
     */
    public int getFlattenedGroupIndex(ExpandableListPosition listPosition) {
        int groupIndex = listPosition.groupPos;
        int runningTotal = 0;

        for (int i = 0; i < groupIndex; i++) {
            runningTotal += numberOfVisibleItemsInGroup(i);
        }
        return runningTotal;
    }

    /**
     * @param groupIndex 表示 {@link #groups} 内组的索引
     * @return {@link #getVisibleItemCount()} 中组的索引
     */
    public int getFlattenedGroupIndex(int groupIndex) {
        int runningTotal = 0;

        for (int i = 0; i < groupIndex; i++) {
            runningTotal += numberOfVisibleItemsInGroup(i);
        }
        return runningTotal;
    }

    /**
     * @param group {@link ExpandableGroup} 内的 {@link #groups}
     * @return {@link #getVisibleItemCount()} 中组的索引，如果 groups.indexOf 找不到组，则返回 0
     */
    public int getFlattenedGroupIndex(ExpandableGroup group) {
        int groupIndex = groups.indexOf(group);
        int runningTotal = 0;

        for (int i = 0; i < groupIndex; i++) {
            runningTotal += numberOfVisibleItemsInGroup(i);
        }
        return runningTotal;
    }

    /**
     * 将子位置转换为平面列表位置。
     *
     * @param packedPosition 要在其打包位置表示中转换的子位置。
     * @return 给定孩子的平面列表位置
     */
    public int getFlattenedChildIndex(long packedPosition) {
        ExpandableListPosition listPosition = ExpandableListPosition.obtainPosition(packedPosition);
        return getFlattenedChildIndex(listPosition);
    }

    /**
     * 将子位置转换为平面列表位置。
     *
     * @param listPosition 要在其 {@link ExpandableListPosition} 表示中转换的子位置。
     * @return 给定孩子的平面列表位置
     */
    public int getFlattenedChildIndex(ExpandableListPosition listPosition) {
        int groupIndex = listPosition.groupPos;
        int childIndex = listPosition.childPos;
        int runningTotal = 0;

        for (int i = 0; i < groupIndex; i++) {
            runningTotal += numberOfVisibleItemsInGroup(i);
        }
        return runningTotal + childIndex + 1;
    }

    /**
     * 将子位置的详细信息转换为平面列表位置。
     *
     * @param groupIndex {@link #groups} 中组的索引
     * @param childIndex 子项在 {@link ExpandableGroup} 中的索引
     * @return 给定孩子的平面列表位置
     */
    public int getFlattenedChildIndex(int groupIndex, int childIndex) {
        int runningTotal = 0;

        for (int i = 0; i < groupIndex; i++) {
            runningTotal += numberOfVisibleItemsInGroup(i);
        }
        return runningTotal + childIndex + 1;
    }

    /**
     * @param groupIndex {@link #groups} 中组的索引
     * @return 组中第一个孩子的平面列表位置
     */
    public int getFlattenedFirstChildIndex(int groupIndex) {
        return getFlattenedGroupIndex(groupIndex) + 1;
    }

    /**
     * @param listPosition 要在其 {@link ExpandableListPosition} 表示中转换的子位置。
     * @return 组中第一个孩子的平面列表位置
     */
    public int getFlattenedFirstChildIndex(ExpandableListPosition listPosition) {
        return getFlattenedGroupIndex(listPosition) + 1;
    }

    /**
     * @param listPosition 一个 {@link ExpandableListPosition} 代表一个孩子或组
     * @return 与@param listPosition 关联的组内的孩子总数
     */
    public int getExpandableGroupItemCount(ExpandableListPosition listPosition) {
        return groups.get(listPosition.groupPos).getItemCount();
    }

    /**
     * 将组 pos 或子 pos 转换为 {@link ExpandableGroup}。 如果 {@link ExpandableListPosition} 是子位置，则返回它所属的 {@link ExpandableGroup}
     *
     * @param listPosition a {@link ExpandableListPosition} 表示组位置或子位置
     * @return 包含 listPosition 的 {@link ExpandableGroup} 对象
     */
    public ExpandableGroup getExpandableGroup(ExpandableListPosition listPosition) {
        return groups.get(listPosition.groupPos);
    }
}
