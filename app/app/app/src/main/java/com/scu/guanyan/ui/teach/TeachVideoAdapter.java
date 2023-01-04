package com.scu.guanyan.ui.teach;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.scu.guanyan.R;
import com.scu.guanyan.base.RecyclerViewHolder;
import com.scu.guanyan.bean.TeachVideo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: Guanyan
 * @author: 陈博文
 * @create: 2022-11-12 16:28
 * @description:
 **/
public class TeachVideoAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private Context mContext;
    private List<TeachVideo> mItems;

    public TeachVideoAdapter(Context context) {
        super();
        this.mContext = context;
        mItems = new ArrayList<>();
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_teach_video, parent, false);
        return new TeachVideoViewHolder(mContext,view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerViewHolder holder, int position) {
        TeachVideo item = mItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public void addData(List<TeachVideo> data){
        if(data == null) return;
        mItems.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(TeachVideo data){
        if(data == null) return;
        mItems.add(data);
        notifyDataSetChanged();
    }

    public void deleteData(int index){
        mItems.remove(index);
        notifyDataSetChanged();
    }

    public void deleteData(Object obj){
        mItems.remove(obj);
        notifyDataSetChanged();
    }

    public void clearData(){
        mItems.clear();
        notifyDataSetChanged();
    }
}
