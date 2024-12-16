package com.example.howmanyset.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.howmanyset.model.Exercise;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExerciseStateManager {
    private static final String PREF_NAME = "ExerciseState";
    private static final String KEY_EXERCISES = "exercises";
    
    private final SharedPreferences preferences;
    
    public ExerciseStateManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void saveExercises(List<Exercise> exercises) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Exercise exercise : exercises) {
                jsonArray.put(exercise.toJson());
            }
            preferences.edit()
                    .putString(KEY_EXERCISES, jsonArray.toString())
                    .apply();
        } catch (JSONException e) {
            Log.e("ExerciseStateManager", "Error saving exercises", e);
        }
    }
    
    public List<Exercise> loadExercises() {
        List<Exercise> exercises = new ArrayList<>();
        String jsonStr = preferences.getString(KEY_EXERCISES, null);
        
        if (jsonStr != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    exercises.add(Exercise.fromJson(jsonObject));
                }
            } catch (JSONException e) {
                Log.e("ExerciseStateManager", "Error loading exercises", e);
            }
        }
        
        return exercises;
    }
    
    public void clearExercises() {
        preferences.edit().remove(KEY_EXERCISES).apply();
    }
} 