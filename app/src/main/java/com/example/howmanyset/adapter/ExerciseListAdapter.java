package com.example.howmanyset.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.howmanyset.R;
import com.example.howmanyset.model.Exercise;
import java.util.List;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.ViewHolder> {
    private List<Exercise> exercises;
    private OnExerciseClickListener listener;

    public interface OnExerciseClickListener {
        void onExerciseClick(int position);
        void onEditClick(int position);
    }

    public ExerciseListAdapter(List<Exercise> exercises, OnExerciseClickListener listener) {
        this.exercises = exercises;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_list, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.exerciseName.setText(exercise.getName());
        holder.exerciseDetails.setText(
            String.format("%d세트 x %d회 / %.1fkg / 휴식 %d초",
                exercise.getTotalSets(),
                exercise.getReps(),
                exercise.getWeight(),
                exercise.getRestTime() / 1000
            )
        );
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        TextView exerciseDetails;
        ImageButton btnEdit;

        public ViewHolder(View itemView, OnExerciseClickListener listener) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            exerciseDetails = itemView.findViewById(R.id.exerciseDetails);
            btnEdit = itemView.findViewById(R.id.btnEdit);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onExerciseClick(getAdapterPosition());
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onEditClick(getAdapterPosition());
                }
            });
        }
    }
} 