package com.thoughtbot.expandablecheckrecyclerview.viewholders;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Checkable;
import com.thoughtbot.expandablecheckrecyclerview.listeners.OnChildCheckChangedListener;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

/**
 * ChildViewHolder 的一个实例，它有一个 Checkable 小部件，以便此视图可以具有选中和未选中状态
 */
public abstract class CheckableChildViewHolder extends ChildViewHolder implements OnClickListener {

  private OnChildCheckChangedListener listener;
  private Checkable checkable;

  public CheckableChildViewHolder(View itemView) {
    super(itemView);
    itemView.setOnClickListener(this);
  }

  /**
   * @param flatPos 这个 CheckableChildViewHolder 在 RecyclerView 中的原始索引
   * @param 检查状态以设置 Checkable 小部件以查看 ChildCheckController#isChildChecked(ExpandableListPosition)
   */
  public void onBindViewHolder(int flatPos, boolean checked) {
    checkable = getCheckable();
    checkable.setChecked(checked);
  }

  @Override
  public void onClick(View v) {
    checkable.toggle();
    if (listener != null) {
      listener.onChildCheckChanged(v, checkable.isChecked(), getAdapterPosition());
    }
  }

  public void setOnChildCheckedListener(OnChildCheckChangedListener listener) {
    this.listener = listener;
  }

  /**
   * 在 {@link #onBindViewHolder(int, boolean)} 期间调用
   *
   * 返回与此 ViewHolder 关联的 {@link Checkable} 小部件
   */
  public abstract Checkable getCheckable();
}