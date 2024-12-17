package com.example.howmanyset;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.howmanyset.adapter.ExerciseListAdapter;
import com.example.howmanyset.model.Exercise;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;
import android.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

public class DetailActivity extends AppCompatActivity implements ExerciseListAdapter.OnExerciseClickListener {
    private RecyclerView exerciseRecyclerView;
    private ExerciseListAdapter adapter;
    private List<Exercise> exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView);

        toolbar.setNavigationOnClickListener(v -> finish());

        // MainActivity에서 현재 루틴 정보 가져오기
        String routineName = getIntent().getStringExtra("routine_name");
        if (routineName != null) {
            toolbar.setTitle(routineName);
        }

        // MainActivity의 참조 얻기
        MainActivity mainActivity = (MainActivity) getParent();
        if (mainActivity != null) {
            exercises = mainActivity.getCurrentRoutineExercises();
            adapter = new ExerciseListAdapter(exercises, this);
            exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            exerciseRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onExerciseClick(int position) {
        // 운동 클릭 시 동작 (필요한 경우 구현)
    }

    @Override
    public void onEditClick(int position) {
        showEditExerciseDialog(position);
    }

    private void showEditExerciseDialog(int position) {
        Exercise exercise = exercises.get(position);
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

        new AlertDialog.Builder(this)
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
} 