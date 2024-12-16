package com.example.howmanyset.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.howmanyset.R;
import com.example.howmanyset.model.Routine;
import java.util.List;

public class RoutineListAdapter extends RecyclerView.Adapter<RoutineListAdapter.ViewHolder> {
    private List<Routine> routines;
    private OnRoutineClickListener listener;

    public interface OnRoutineClickListener {
        void onRoutineClick(int position);
        void onEditClick(int position);
    }

    public RoutineListAdapter(List<Routine> routines, OnRoutineClickListener listener) {
        this.routines = routines;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_routine_list, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Routine routine = routines.get(position);
        holder.routineName.setText(routine.getName());
        holder.exerciseCount.setText(routine.getExercises().size() + "개의 운동");
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView routineName;
        TextView exerciseCount;
        ImageButton btnEdit;

        public ViewHolder(View itemView, OnRoutineClickListener listener) {
            super(itemView);
            routineName = itemView.findViewById(R.id.routineName);
            exerciseCount = itemView.findViewById(R.id.exerciseCount);
            btnEdit = itemView.findViewById(R.id.btnEdit);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onRoutineClick(getAdapterPosition());
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