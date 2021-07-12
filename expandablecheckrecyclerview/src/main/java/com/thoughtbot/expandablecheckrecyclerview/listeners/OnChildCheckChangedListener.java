package com.thoughtbot.expandablecheckrecyclerview.listeners;

//import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.thoughtbot.expandablecheckrecyclerview.viewholders.CheckableChildViewHolder;

/**
 * 单击 CheckableChildViewHolder#checkable 时调用回调的接口定义。
 */
public interface OnChildCheckChangedListener {

  /**
   * @param checked 视图的当前选中状态
   * @param flatPos 子元素在 RecyclerView 中的平面位置（原始索引）
   */
  void onChildCheckChanged(View view, boolean checked, int flatPos);

}