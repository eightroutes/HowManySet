package com.example.howmanyset.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.howmanyset.MainActivity;
import com.example.howmanyset.R;
import com.example.howmanyset.model.Exercise;
import com.example.howmanyset.model.Routine;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.ViewHolder> {

    private List<Exercise> exercises;
    private ExerciseListener exerciseListener;
    private ViewPager2 viewPager;
    private CountDownTimer timer;
    private Context context;

    public RoutineAdapter(List<Exercise> exercises, ExerciseListener listener, ViewPager2 viewPager) {
        this.exercises = exercises;
        this.exerciseListener = listener;
        this.viewPager = viewPager;
        this.context = viewPager.getContext();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routine, parent, false);
        return new ViewHolder(v, exerciseListener);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.exerciseName.setText(exercise.getName());
        
        if (exercise.getCurrentSet() > exercise.getTotalSets()) {
            holder.progressArc.setProgress(100);
            holder.setsLeft.setText("운동 완료!");
            holder.btnSkipRest.setVisibility(View.GONE);
            holder.restTime.setVisibility(View.VISIBLE);
            holder.restTime.setText(String.format("휴식시간: %d초", exercise.getRestTime() / 1000));
        } else {
            String setsText = String.format("%d/%d 세트", 
                exercise.getCurrentSet(), exercise.getTotalSets());
            holder.setsLeft.setText(setsText);
            
            int progress = (int)((float)(exercise.getCurrentSet() - 1) / exercise.getTotalSets() * 100);
            holder.progressArc.setProgress(progress);
            
            holder.restTime.setVisibility(View.VISIBLE);
            holder.restTime.setText(String.format("휴식시간: %d초", exercise.getRestTime() / 1000));
        }
        
        holder.weightInfo.setText(String.format("무게: %dkg", (int)exercise.getWeight()));
        
        if (exercise.isResting()) {
            holder.btnSkipRest.setVisibility(View.VISIBLE);
            updateRestTimer(holder, exercise);
        } else {
            holder.btnSkipRest.setVisibility(View.GONE);
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
                holder.setsLeft.setText(String.format("휴식: %d초 남음", millisUntilFinished / 1000));
                holder.restTime.setText(String.format("휴식시간: %d초", exercise.getRestTime() / 1000));
            }

            @Override
            public void onFinish() {
                exercise.setResting(false);
                exercise.setRemainingRestTime(0);
                holder.btnSkipRest.setVisibility(View.GONE);
                
                if (exercise.getCurrentSet() > exercise.getTotalSets()) {
                    holder.progressArc.setProgress(100);
                    holder.setsLeft.setText("운동 완료!");
                    holder.restTime.setVisibility(View.VISIBLE);
                    holder.restTime.setText(String.format("휴식시간: %d초", exercise.getRestTime() / 1000));
                } else {
                    holder.setsLeft.setText(String.format("%d/%d 세트", 
                        exercise.getCurrentSet(), exercise.getTotalSets()));
                    holder.restTime.setText(String.format("휴식시간: %d초", exercise.getRestTime() / 1000));
                }
                
                if (exerciseListener != null) {
                    exerciseListener.onRestFinished(holder.getAdapterPosition());
                }
            }
        }.start();
    }

    public void startRest(int position) {
        Exercise exercise = exercises.get(position);
        exercise.completeSet();
        notifyItemChanged(position);
    }

    public interface ExerciseListener {
        void onSetCompleted(int position);
        void onRestFinished(int position);
        void onRestSkipped(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        TextView setsLeft;
        TextView weightInfo;
        TextView restTime;
        ProgressBar progressArc;
        ImageButton btnSkipRest;

        public ViewHolder(View itemView, ExerciseListener listener) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            setsLeft = itemView.findViewById(R.id.setsLeft);
            weightInfo = itemView.findViewById(R.id.weightInfo);
            restTime = itemView.findViewById(R.id.restTime);
            progressArc = itemView.findViewById(R.id.progressArc);
            btnSkipRest = itemView.findViewById(R.id.btnSkipRest);



            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onSetCompleted(getAdapterPosition());
                }
            });

            btnSkipRest.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    Exercise exercise = exercises.get(getAdapterPosition());
                    if (exercise.isResting()) {
                        if (timer != null) {
                            timer.cancel();
                        }
                        exercise.setResting(false);
                        exercise.setRemainingRestTime(0);
                        btnSkipRest.setVisibility(View.GONE);
                        listener.onRestSkipped(getAdapterPosition());
                    }
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

    private void deleteRoutine(int position) {
        if (position >= 0 && position < exercises.size()) {
            try {
                exercises.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, exercises.size());
                
                if (context instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) context;
                    Routine currentRoutine = mainActivity.getCurrentRoutine();
                    if (currentRoutine != null) {
                        currentRoutine.setExercises(exercises);
                        mainActivity.setCurrentRoutine(currentRoutine);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(context, "루틴 삭제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onPause() {
        try {
            if (exercises != null && !exercises.isEmpty() && context instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) context;
                Routine currentRoutine = mainActivity.getCurrentRoutine();
                if (currentRoutine != null) {
                    currentRoutine.setExercises(exercises);
                    mainActivity.setCurrentRoutine(currentRoutine);
                }
            }
        } catch (Exception e) {
            Log.e("RoutineAdapter", "루틴 저장 중 오류 발생", e);
        }
    }
}