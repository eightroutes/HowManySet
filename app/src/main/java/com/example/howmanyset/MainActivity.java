package com.example.howmanyset;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.howmanyset.adapter.RoutineAdapter;
import com.example.howmanyset.model.Exercise;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import com.example.howmanyset.manager.ExerciseStateManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.fragment.app.Fragment;

import com.example.howmanyset.fragments.WorkoutFragment;
import com.example.howmanyset.fragments.RoutineFragment;
import com.example.howmanyset.model.Routine;
import com.example.howmanyset.manager.RoutineManager;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RoutineManager routineManager;
    private Routine currentRoutine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        routineManager = new RoutineManager(this);
        
        // 저장된 루틴들을 불러오고, 마지막으로 사용한 루틴을 설정
        List<Routine> routines = routineManager.loadRoutines();
        if (!routines.isEmpty()) {
            currentRoutine = routines.get(routines.size() - 1);  // 마지막 루틴을 현재 루틴으로 설정
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            
            if (item.getItemId() == R.id.navigation_workout) {
                selectedFragment = new WorkoutFragment();
            } else if (item.getItemId() == R.id.navigation_routine) {
                selectedFragment = new RoutineFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            }
            return true;
        });

        // 기본 프래그먼트 설정
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_workout);
        }
    }

    public RoutineManager getRoutineManager() {
        return routineManager;
    }

    public Routine getCurrentRoutine() {
        return currentRoutine;
    }

    public void setCurrentRoutine(Routine routine) {
        this.currentRoutine = routine;
        // 현재 루틴이 변경될 때마다 저장
        List<Routine> routines = routineManager.loadRoutines();
        for (int i = 0; i < routines.size(); i++) {
            if (routines.get(i).getName().equals(routine.getName())) {
                routines.set(i, routine);
                break;
            }
        }
        routineManager.saveRoutines(routines);
    }

    public List<Exercise> getCurrentRoutineExercises() {
        return currentRoutine != null ? currentRoutine.getExercises() : new ArrayList<>();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 앱이 백그라운드로 갈 때도 저장
        if (currentRoutine != null) {
            List<Routine> routines = routineManager.loadRoutines();
            for (int i = 0; i < routines.size(); i++) {
                if (routines.get(i).getName().equals(currentRoutine.getName())) {
                    routines.set(i, currentRoutine);
                    break;
                }
            }
            routineManager.saveRoutines(routines);
        }
    }
}