package com.example.howmanyset.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Routine {
    private String name;
    private List<Exercise> exercises;

    public Routine(String name) {
        this.name = name;
        this.exercises = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public void addExercise(Exercise exercise) {
        exercises.add(exercise);
    }

    public void removeExercise(int position) {
        exercises.remove(position);
    }

    public void clearExercises() {
        exercises.clear();
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", name);
        
        JSONArray exercisesJson = new JSONArray();
        for (Exercise exercise : exercises) {
            JSONObject exerciseJson = new JSONObject();
            exerciseJson.put("name", exercise.getName());
            exerciseJson.put("totalSets", exercise.getTotalSets());
            exerciseJson.put("currentSet", exercise.getCurrentSet());
            exerciseJson.put("reps", exercise.getReps());
            exerciseJson.put("weight", exercise.getWeight());
            exerciseJson.put("restTime", exercise.getRestTime());
            exerciseJson.put("isResting", exercise.isResting());
            exerciseJson.put("remainingRestTime", exercise.getRemainingRestTime());
            exercisesJson.put(exerciseJson);
        }
        json.put("exercises", exercisesJson);
        
        return json;
    }

    public static Routine fromJson(JSONObject json) throws JSONException {
        Routine routine = new Routine(json.getString("name"));
        
        JSONArray exercisesJson = json.getJSONArray("exercises");
        for (int i = 0; i < exercisesJson.length(); i++) {
            JSONObject exerciseJson = exercisesJson.getJSONObject(i);
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
        
        return routine;
    }
} 