package com.example.sairam.xraybot;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter <MessageAdapter.CustomViewHolder> {

    class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public CustomViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.text_message);
        }
    }
    List<Message> messageList;

    public MessageAdapter(List<Message> messageList){
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new MessageAdapter.CustomViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(viewType, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.CustomViewHolder customViewHolder, int position) {
        customViewHolder.textView.setText(messageList.get(position).getMsg());
    }

    @Override
    public int getItemViewType(int position) {
        if(messageList.get(position).isMe()){
            return R.layout.user_message;
        }
        else{
            return R.layout.bot_message;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
