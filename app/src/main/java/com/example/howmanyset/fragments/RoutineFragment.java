package com.example.howmanyset.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.howmanyset.MainActivity;
import com.example.howmanyset.R;
import com.example.howmanyset.adapter.RoutineListAdapter;
import com.example.howmanyset.model.Routine;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class RoutineFragment extends Fragment implements RoutineListAdapter.OnRoutineClickListener {
    private RecyclerView routineRecyclerView;
    private FloatingActionButton fabAddRoutine;
    private RoutineListAdapter adapter;
    private List<Routine> routines;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);
        
        routineRecyclerView = view.findViewById(R.id.routineRecyclerView);
        fabAddRoutine = view.findViewById(R.id.fabAddRoutine);
        
        routines = ((MainActivity) requireActivity()).getRoutineManager().loadRoutines();
        adapter = new RoutineListAdapter(routines, this);
        
        routineRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        routineRecyclerView.setAdapter(adapter);
        
        fabAddRoutine.setOnClickListener(v -> showAddRoutineDialog());
        
        return view;
    }

    private void showAddRoutineDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_routine, null);
        TextInputEditText editRoutineName = dialogView.findViewById(R.id.editRoutineName);

        new AlertDialog.Builder(requireContext())
            .setTitle("새 루틴 추가")
            .setView(dialogView)
            .setPositiveButton("추가", (dialog, which) -> {
                String routineName = editRoutineName.getText().toString();
                if (!routineName.isEmpty()) {
                    routines.add(new Routine(routineName));
                    adapter.notifyItemInserted(routines.size() - 1);
                }
            })
            .setNegativeButton("취소", null)
            .show();
    }

    @Override
    public void onRoutineClick(int position) {
        // 루틴 선택 시 운동 목록으로 이동
        ((MainActivity) requireActivity()).setCurrentRoutine(routines.get(position));
        requireActivity().getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, new ExerciseListFragment())
            .addToBackStack(null)
            .commit();
    }

    @Override
    public void onEditClick(int position) {
        new AlertDialog.Builder(requireContext())
            .setTitle("루틴 관리")
            .setItems(new String[]{"수정", "삭제"}, (dialog, which) -> {
                if (which == 0) {
                    showEditRoutineDialog(position);
                } else {
                    new AlertDialog.Builder(requireContext())
                        .setTitle("루틴 삭제")
                        .setMessage("이 루틴을 삭제하시겠습니까?")
                        .setPositiveButton("삭제", (dialogInterface, i) -> {
                            routines.remove(position);
                            adapter.notifyItemRemoved(position);
                            ((MainActivity) requireActivity()).getRoutineManager().saveRoutines(routines);
                            
                            // 현재 루틴이 삭제된 루틴이라면 null로 설정
                            Routine currentRoutine = ((MainActivity) requireActivity()).getCurrentRoutine();
                            if (currentRoutine != null && currentRoutine.getName().equals(routines.get(position).getName())) {
                                ((MainActivity) requireActivity()).setCurrentRoutine(null);
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
                }
            })
            .show();
    }

    private void showEditRoutineDialog(int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_routine, null);
        TextInputEditText editRoutineName = dialogView.findViewById(R.id.editRoutineName);
        editRoutineName.setText(routines.get(position).getName());

        new AlertDialog.Builder(requireContext())
            .setTitle("루틴 수정")
            .setView(dialogView)
            .setPositiveButton("수정", (dialog, which) -> {
                String routineName = editRoutineName.getText().toString();
                if (!routineName.isEmpty()) {
                    routines.get(position).setName(routineName);
                    adapter.notifyItemChanged(position);
                }
            })
            .setNegativeButton("취소", null)
            .show();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) requireActivity()).getRoutineManager().saveRoutines(routines);
    }
} 