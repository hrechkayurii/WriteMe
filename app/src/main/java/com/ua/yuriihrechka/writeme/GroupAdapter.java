package com.ua.yuriihrechka.writeme;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder>{

    private List<GroupModel> mList;

    public GroupAdapter(List<GroupModel> list) {
        mList = list;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new GroupViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rs_fragment_groups, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder groupViewHolder, int i) {

        GroupModel group = mList.get(i);
        groupViewHolder.mTextView.setText(group.name);


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class GroupViewHolder extends RecyclerView.ViewHolder{

        TextView mTextView;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = (TextView)itemView.findViewById(R.id.textView);
        }
    }

}
