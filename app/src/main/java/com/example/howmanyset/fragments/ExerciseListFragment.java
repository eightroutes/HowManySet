package com.example.howmanyset.fragments;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.howmanyset.MainActivity;
import com.example.howmanyset.R;
import com.example.howmanyset.adapter.ExerciseListAdapter;
import com.example.howmanyset.model.Exercise;
import com.example.howmanyset.model.Routine;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class ExerciseListFragment extends Fragment implements ExerciseListAdapter.OnExerciseClickListener {
    private RecyclerView exerciseRecyclerView;
    private FloatingActionButton fabAddExercise;
    private ExerciseListAdapter adapter;
    private Routine currentRoutine;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_exercise_list, container, false);
        
        exerciseRecyclerView = rootView.findViewById(R.id.exerciseRecyclerView);
        fabAddExercise = rootView.findViewById(R.id.fabAddExercise);
        MaterialButton btnStartWorkout = rootView.findViewById(R.id.btnStartWorkout);
        
        currentRoutine = ((MainActivity) requireActivity()).getCurrentRoutine();
        
        updateStartButtonState(btnStartWorkout);
        
        adapter = new ExerciseListAdapter(currentRoutine.getExercises(), this);
        
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        exerciseRecyclerView.setAdapter(adapter);
        
        fabAddExercise.setOnClickListener(v -> showAddExerciseDialog());
        
        btnStartWorkout.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).setCurrentRoutine(currentRoutine);
            
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
            bottomNav.setSelectedItemId(R.id.navigation_workout);
        });
        
        return rootView;
    }

    private void updateStartButtonState(MaterialButton button) {
        if (currentRoutine == null) {
            button.setEnabled(false);
            button.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.text_secondary)
            ));
            return;
        }

        boolean hasExercises = !currentRoutine.getExercises().isEmpty();
        button.setEnabled(hasExercises);
        button.setBackgroundTintList(ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), 
                hasExercises ? R.color.green : R.color.text_secondary)
        ));
    }

    private void showAddExerciseDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_exercise, null);
        EditText editName = dialogView.findViewById(R.id.editExerciseName);
        EditText editSets = dialogView.findViewById(R.id.editSets);
        EditText editReps = dialogView.findViewById(R.id.editReps);
        EditText editWeight = dialogView.findViewById(R.id.editWeight);
        EditText editRestTime = dialogView.findViewById(R.id.editRestTime);
        MaterialButton btnStartWorkout = rootView.findViewById(R.id.btnStartWorkout);

        new AlertDialog.Builder(requireContext())
            .setTitle("운동 추가")
            .setView(dialogView)
            .setPositiveButton("추가", (dialog, which) -> {
                if (validateInputs(editName, editSets, editReps, editWeight, editRestTime)) {
                    Exercise exercise = new Exercise();
                    exercise.setName(editName.getText().toString());
                    exercise.setTotalSets(Integer.parseInt(editSets.getText().toString()));
                    exercise.setReps(Integer.parseInt(editReps.getText().toString()));
                    exercise.setWeight(Integer.parseInt(editWeight.getText().toString()));
                    exercise.setRestTime(Integer.parseInt(editRestTime.getText().toString()) * 1000);
                    
                    currentRoutine.addExercise(exercise);
                    adapter.notifyItemInserted(currentRoutine.getExercises().size() - 1);
                    
                    ((MainActivity) requireActivity()).setCurrentRoutine(currentRoutine);
                    
                    updateStartButtonState(btnStartWorkout);
                }
            })
            .setNegativeButton("취소", null)
            .show();
    }

    private boolean validateInputs(EditText... editTexts) {
        for (EditText editText : editTexts) {
            if (editText.getText().toString().trim().isEmpty()) {
                editText.setError("필수 입력 항목입니다");
                return false;
            }
        }
        return true;
    }

    @Override
    public void onExerciseClick(int position) {
        // 운동 상세 정보 표시 또는 다른 동작 수행
    }

    @Override
    public void onEditClick(int position) {
        new AlertDialog.Builder(requireContext())
            .setTitle("운동 관리")
            .setItems(new String[]{"수정", "삭제"}, (dialog, which) -> {
                if (which == 0) {
                    showEditExerciseDialog(position);
                } else {
                    new AlertDialog.Builder(requireContext())
                        .setTitle("운동 삭제")
                        .setMessage("이 운동을 삭제하시겠습니까?")
                        .setPositiveButton("삭제", (dialogInterface, i) -> {
                            currentRoutine.getExercises().remove(position);
                            adapter.notifyItemRemoved(position);
                            ((MainActivity) requireActivity()).setCurrentRoutine(currentRoutine);
                            updateStartButtonState(rootView.findViewById(R.id.btnStartWorkout));
                        })
                        .setNegativeButton("취소", null)
                        .show();
                }
            })
            .show();
    }

    private void showEditExerciseDialog(int position) {
        Exercise exercise = currentRoutine.getExercises().get(position);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_exercise, null);
        
        EditText editName = dialogView.findViewById(R.id.editExerciseName);
        EditText editSets = dialogView.findViewById(R.id.editSets);
        EditText editReps = dialogView.findViewById(R.id.editReps);
        EditText editWeight = dialogView.findViewById(R.id.editWeight);
        EditText editRestTime = dialogView.findViewById(R.id.editRestTime);

        // 기존 값 설정
        editName.setText(exercise.getName());
        editSets.setText(String.valueOf(exercise.getTotalSets()));
        editReps.setText(String.valueOf(exercise.getReps()));
        editWeight.setText(String.valueOf(exercise.getWeight()));
        editRestTime.setText(String.valueOf(exercise.getRestTime() / 1000));

        new AlertDialog.Builder(requireContext())
            .setTitle("운동 수정")
            .setView(dialogView)
            .setPositiveButton("수정", (dialog, which) -> {
                if (validateInputs(editName, editSets, editReps, editWeight, editRestTime)) {
                    exercise.setName(editName.getText().toString());
                    exercise.setTotalSets(Integer.parseInt(editSets.getText().toString()));
                    exercise.setReps(Integer.parseInt(editReps.getText().toString()));
                    exercise.setWeight(Integer.parseInt(editWeight.getText().toString()));
                    exercise.setRestTime(Integer.parseInt(editRestTime.getText().toString()) * 1000);
                    
                    adapter.notifyItemChanged(position);
                    
                    ((MainActivity) requireActivity()).setCurrentRoutine(currentRoutine);
                }
            })
            .setNegativeButton("취소", null)
            .show();
    }
} 