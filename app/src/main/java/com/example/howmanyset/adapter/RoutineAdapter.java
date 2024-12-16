package com.example.howmanyset.adapter;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.howmanyset.R;
import com.example.howmanyset.model.Exercise;

import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.ViewHolder> {

    private List<Exercise> exercises;
    private OnItemClickListener listener;
    private CountDownTimer timer;

    public RoutineAdapter(List<Exercise> exercises, OnItemClickListener listener) {
        this.exercises = exercises;
        this.listener = listener;
    }

    @Override
    public RoutineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routine, parent, false);
        return new ViewHolder(v, listener);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.exerciseName.setText(exercise.getName());
        
        String setsText = String.format("세트 %d/%d", 
            exercise.getCurrentSet(), exercise.getTotalSets());
        holder.setsLeft.setText(setsText);
        
        holder.progressArc.setProgress(exercise.getProgress());
        
        if (exercise.isResting()) {
            updateRestTimer(holder, exercise);
        }
    }

    private void updateRestTimer(ViewHolder holder, Exercise exercise) {
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(exercise.getRemainingRestTime(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                exercise.setRemainingRestTime(millisUntilFinished);
                holder.setsLeft.setText(String.format("휴식: %d초", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                exercise.setResting(false);
                notifyItemChanged(holder.getAdapterPosition());
                
                if (exercise.getCurrentSet() > exercise.getTotalSets() && 
                    holder.getAdapterPosition() < exercises.size() - 1) {
                    ViewPager2 viewPager = (ViewPager2) holder.itemView.getParent().getParent();
                    viewPager.setCurrentItem(holder.getAdapterPosition() + 1, true);
                }
            }
        }.start();
    }

    public void startRest(int position) {
        Exercise exercise = exercises.get(position);
        exercise.completeSet();
        notifyItemChanged(position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        ProgressBar progressArc;
        TextView setsLeft;
        TextView weightInfo;
        TextView restTime;

        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            progressArc = itemView.findViewById(R.id.progressArc);
            setsLeft = itemView.findViewById(R.id.setsLeft);
            weightInfo = itemView.findViewById(R.id.weightInfo);
            restTime = itemView.findViewById(R.id.restTime);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}