package com.example.administrator.GoAndDo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder> {

    private Context mCtx;
    private List<Task> taskList;

    public TasksAdapter(Context mCtx, List<Task> taskList) {
        this.mCtx = mCtx;
        this.taskList = taskList;
    }

    @Override
    public TasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_tasks, parent, false);
        return new TasksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TasksViewHolder holder, int position) {
        Task t = taskList.get(position);
        holder.textViewTask.setText(t.getTask());
        holder.textViewDesc.setText(t.getDesc());
        holder.textViewTime.setText(t.getTime());
        holder.textViewDate.setText(t.getDate());
        if(t.getImage()!=null)
        {
            holder.imageViewPic.setImageURI(Uri.parse(t.getImage()));
        }



        if (t.isFinished())
        {
            holder.textViewStatus.setText("Completed");
            holder.textViewStatus.setTextColor(Color.parseColor("#6F6F6F"));
            holder.textViewStatus.setBackgroundColor(Color.parseColor("#FFFF11"));
        }

        else
        {
            holder.textViewStatus.setText("Not Completed");
            holder.textViewStatus.setTextColor(Color.parseColor("#FFFFFF"));
            holder.textViewStatus.setBackgroundColor(Color.parseColor("#6F6F6F"));
        }

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class TasksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewStatus, textViewTask, textViewDesc, textViewTime, textViewDate;

        ImageView imageViewPic;


        public TasksViewHolder(View itemView) {
            super(itemView);

            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewTask = itemView.findViewById(R.id.textViewTask);
            textViewDesc = itemView.findViewById(R.id.textViewDesc);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            imageViewPic = itemView.findViewById(R.id.imageViewPic);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Task task = taskList.get(getAdapterPosition());

            Intent intent = new Intent(mCtx, UpdateTaskActivity.class);
            intent.putExtra("task", task);

            mCtx.startActivity(intent);
        }
    }
}
