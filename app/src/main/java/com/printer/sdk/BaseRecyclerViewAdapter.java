package com.printer.sdk;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

public abstract class BaseRecyclerViewAdapter<T extends BaseRecyclerViewAdapter.BaseRecyclerViewAdapterHolder, D> extends RecyclerView.Adapter<T> {

    Context context;

    List<D> dates = null;

    private OnClickItemLister onClickItemLister;

    public void setOnClickItemLister(OnClickItemLister onClickItemLister) {
        this.onClickItemLister = onClickItemLister;
    }

    private OnClickLongItemLister onClickLongItemLister;

    public void setOnClickLongItemLister(OnClickLongItemLister onClickLongItemLister) {
        this.onClickLongItemLister = onClickLongItemLister;
    }

    public BaseRecyclerViewAdapter(Context context, List<D> dates) {
        this.context = context;
        this.dates = dates;
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

//    @Override
//    public void onBindViewHolder(@NonNull T holder, int position) {
//    }

    public class BaseRecyclerViewAdapterHolder extends RecyclerView.ViewHolder {

        public BaseRecyclerViewAdapterHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position == -1){
                        return;
                    }
                    if (onClickItemLister != null) {
                        onClickItemLister.onClick(itemView, position);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if(position == -1){
                        return false;
                    }
                    if (onClickLongItemLister != null) {
                        return onClickLongItemLister.onLongClick(itemView, getAdapterPosition());
                    }
                    return false;
                }
            });

        }
    }

    public interface OnClickItemLister {
        void onClick(View view, int position);
    }

    public interface OnClickLongItemLister {
        boolean onLongClick(View view, int position);
    }

}
