package com.thoughtbot.expandablerecyclerview.viewholders;

//import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.recyclerview.widget.RecyclerView;

import com.thoughtbot.expandablerecyclerview.listeners.OnGroupClickListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

/**
 * {@link ExpandableGroup} 中 {@link ExpandableGroup#title} 的 ViewHolder 当前实现现在允许父视图的子 {@link View} 触发折叠/展开。 *只有 * 父级 {@link View} 上的点击事件会触发折叠或展开
 */
public abstract class GroupViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

  private OnGroupClickListener listener;

  public GroupViewHolder(View itemView) {
    super(itemView);
    itemView.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    if (listener != null) {
      listener.onGroupClick(getAdapterPosition());
    }
  }

  public void setOnGroupClickListener(OnGroupClickListener listener) {
    this.listener = listener;
  }

  public void expand() {}

  public void collapse() {}
}
