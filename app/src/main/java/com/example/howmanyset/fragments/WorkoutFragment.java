package com.example.howmanyset.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.howmanyset.DetailActivity;
import com.example.howmanyset.MainActivity;
import com.example.howmanyset.R;
import com.example.howmanyset.adapter.RoutineAdapter;
import com.example.howmanyset.model.Exercise;
import com.example.howmanyset.model.Routine;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class WorkoutFragment extends Fragment implements RoutineAdapter.ExerciseListener {
    private ViewPager2 viewPager;
    private RoutineAdapter adapter;
    private List<Exercise> exercises;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout, container, false);
        
        viewPager = view.findViewById(R.id.viewPager);
        TextView emptyText = view.findViewById(R.id.emptyText);
        
        exercises = ((MainActivity) requireActivity()).getCurrentRoutineExercises();
        
        if (exercises.isEmpty()) {
            viewPager.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("루틴을 선택해주세요");
        } else {
            viewPager.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
            
            adapter = new RoutineAdapter(exercises, this, viewPager);
            viewPager.setAdapter(adapter);
        }
        
        return view;
    }

    @Override
    public void onSetCompleted(int position) {
        Exercise exercise = exercises.get(position);
        if (!exercise.isResting() && exercise.getCurrentSet() <= exercise.getTotalSets()) {
            adapter.startRest(position);
        }
    }

    @Override
    public void onRestFinished(int position) {
        Exercise exercise = exercises.get(position);
        
        // 모든 세트가 완료되었는지 확인
        if (exercise.getCurrentSet() > exercise.getTotalSets()) {
            adapter.notifyItemChanged(position);
            
            // 다음 운동으로 이동
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (position < exercises.size() - 1) {
                    viewPager.setCurrentItem(position + 1, true);
                }
            }, 800); 
        } else {
            adapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onRestSkipped(int position) {
        Exercise exercise = exercises.get(position);
        if (exercise.getCurrentSet() > exercise.getTotalSets()) {
            adapter.notifyItemChanged(position);
            
            // 다음 운동으로 이동
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (position < exercises.size() - 1) {
                    viewPager.setCurrentItem(position + 1, true);
                }
            }, 800); 
        } else {
            adapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.onDestroy();
        }
    }
} 