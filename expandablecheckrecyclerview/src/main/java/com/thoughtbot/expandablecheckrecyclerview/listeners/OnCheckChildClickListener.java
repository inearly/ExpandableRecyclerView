package com.thoughtbot.expandablecheckrecyclerview.listeners;

//import android.support.v7.widget.RecyclerView;

import android.view.View;

import com.thoughtbot.expandablecheckrecyclerview.CheckableChildRecyclerViewAdapter;
import com.thoughtbot.expandablecheckrecyclerview.models.CheckedExpandableGroup;
import com.thoughtbot.expandablecheckrecyclerview.viewholders.CheckableChildViewHolder;

/**
 * 单击 {@link CheckableChildViewHolder} 时调用回调的接口定义。
 */
public interface OnCheckChildClickListener {
    /**
     * 单击 {@link CheckableChildRecyclerViewAdapter} 中的子项时要调用的回调方法。
     *
     * @param 检查孩子的当前检查状态
     * @param v           被点击的 RecyclerView 中的视图
     * @param group       包含被点击的孩子的 {@link CheckedExpandableGroup}
     * @param childIndex  {@link CheckedExpandableGroup} 中的子位置
     */
    void onCheckChildCLick(View v, boolean checked, CheckedExpandableGroup group, int childIndex);
}
