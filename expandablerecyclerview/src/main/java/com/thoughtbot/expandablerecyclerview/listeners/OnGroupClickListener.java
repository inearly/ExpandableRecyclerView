package com.thoughtbot.expandablerecyclerview.listeners;

//import android.support.v7.widget.RecyclerView;
//import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

public interface OnGroupClickListener {

  /**
   * @param flatPos 平面位置（在可见项目列表中的原始索引GroupViewHolder 的 RecyclerView）
   * @return 如果单击展开组为 false，则单击折叠组为 true
   */
  boolean onGroupClick(int flatPos);
}