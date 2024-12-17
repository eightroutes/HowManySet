package com.example.howmanyset.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Exercise {
    private String name;
    private int totalSets;
    private int currentSet;
    private int reps;
    private double weight;
    private long restTime;
    private boolean isResting;
    private long remainingRestTime;

    public Exercise() {
        this.currentSet = 1;
        this.isResting = false;
    }

    public Exercise(String name, int totalSets, int reps, double weight, long restTime) {
        this.name = name;
        this.totalSets = totalSets;
        this.reps = reps;
        this.weight = weight;
        this.restTime = restTime * 1000;
        this.currentSet = 1;
        this.isResting = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalSets() {
        return totalSets;
    }

    public void setTotalSets(int totalSets) {
        this.totalSets = totalSets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public long getRestTime() {
        return restTime;
    }

    public void setRestTime(long restTime) {
        this.restTime = restTime;
    }

    public int getCurrentSet() {
        return currentSet;
    }

    public void setCurrentSet(int currentSet) {
        this.currentSet = currentSet;
    }

    public boolean isResting() {
        return isResting;
    }

    public void setResting(boolean resting) {
        isResting = resting;
    }

    public long getRemainingRestTime() {
        return remainingRestTime;
    }

    public void setRemainingRestTime(long remainingRestTime) {
        this.remainingRestTime = remainingRestTime;
    }

    public int getProgress() {
        return (int) ((currentSet - 1) * 100.0f / totalSets);
    }

    public void completeSet() {
        if (currentSet <= totalSets) {
            currentSet++;
            isResting = true;
            remainingRestTime = restTime;
        }
    }

    public void resetProgress() {
        currentSet = 1;
        isResting = false;
        remainingRestTime = 0;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("totalSets", totalSets);
        json.put("currentSet", currentSet);
        json.put("reps", reps);
        json.put("weight", weight);
        json.put("restTime", restTime);
        json.put("isResting", isResting);
        json.put("remainingRestTime", remainingRestTime);
        return json;
    }

    public static Exercise fromJson(JSONObject json) throws JSONException {
        Exercise exercise = new Exercise();
        exercise.setName(json.getString("name"));
        exercise.setTotalSets(json.getInt("totalSets"));
        exercise.setReps(json.getInt("reps"));
        exercise.setWeight(json.getInt("weight"));
        exercise.setRestTime(json.getLong("restTime") / 1000);
        exercise.setCurrentSet(json.getInt("currentSet"));
        exercise.setResting(json.getBoolean("isResting"));
        exercise.setRemainingRestTime(json.getLong("remainingRestTime"));
        return exercise;
    }
}