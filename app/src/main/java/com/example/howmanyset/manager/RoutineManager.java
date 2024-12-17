package com.example.howmanyset.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.howmanyset.model.Exercise;
import com.example.howmanyset.model.Routine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class RoutineManager {
    private static final String PREF_NAME = "RoutinePrefs";
    private static final String KEY_ROUTINES = "routines";
    private static final String KEY_LAST_ROUTINE = "last_routine";
    private SharedPreferences preferences;

    public RoutineManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveRoutines(List<Routine> routines) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Routine routine : routines) {
                jsonArray.put(routine.toJson());
            }
            preferences.edit().putString(KEY_ROUTINES, jsonArray.toString()).apply();
            
            if (!routines.isEmpty()) {
                saveLastRoutineName(routines.get(routines.size() - 1).getName());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Routine> loadRoutines() {
        List<Routine> routines = new ArrayList<>();
        try {
            String json = preferences.getString(KEY_ROUTINES, "[]");
            JSONArray jsonArray = new JSONArray(json);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject routineJson = jsonArray.getJSONObject(i);
                Routine routine = new Routine(routineJson.getString("name"));
                
                JSONArray exercisesJson = routineJson.getJSONArray("exercises");
                for (int j = 0; j < exercisesJson.length(); j++) {
                    JSONObject exerciseJson = exercisesJson.getJSONObject(j);
                    Exercise exercise = new Exercise();
                    exercise.setName(exerciseJson.getString("name"));
                    exercise.setTotalSets(exerciseJson.getInt("totalSets"));
                    exercise.setCurrentSet(exerciseJson.getInt("currentSet"));
                    exercise.setReps(exerciseJson.getInt("reps"));
                    exercise.setWeight(exerciseJson.getInt("weight"));
                    exercise.setRestTime(exerciseJson.getLong("restTime"));
                    exercise.setResting(exerciseJson.getBoolean("isResting"));
                    exercise.setRemainingRestTime(exerciseJson.getLong("remainingRestTime"));
                    routine.addExercise(exercise);
                }
                routines.add(routine);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return routines;
    }

    public void clearRoutines() {
        preferences.edit().remove(KEY_ROUTINES).apply();
    }

    public void saveLastRoutineName(String routineName) {
        preferences.edit().putString(KEY_LAST_ROUTINE, routineName).apply();
    }

    public String getLastRoutineName() {
        return preferences.getString(KEY_LAST_ROUTINE, null);
    }
} 