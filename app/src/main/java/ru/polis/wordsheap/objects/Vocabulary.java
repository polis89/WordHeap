package ru.polis.wordsheap.objects;

import android.util.Log;

import java.io.Serializable;

public class Vocabulary implements Serializable{
    public static final String TAG = "VocabularyLog";

    private int id;
    private String name;
    private int countWord;

    private int progressVocabulary;

    public Vocabulary(String name){
        this.name = name;
        countWord = 0;
    }

    public Vocabulary(String name, int countWord){
        this.name = name;
        this.countWord = countWord;
        progressVocabulary = 0;
    }

    public Vocabulary(int id, String name) {
        this.id = id;
        this.name = name;
        countWord = 0;
        progressVocabulary = 0;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public int getCountWord() {
        return countWord;
    }

    public void setCountWord(int countWord) {
        this.countWord = countWord;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProgressVocabulary() {
        Log.i(TAG, "getProgressVocabulary: " + name + ", return " + progressVocabulary);
        return progressVocabulary;
    }

    public void setProgressVocabulary(int progressVocabulary) {
        this.progressVocabulary = progressVocabulary;
    }
    @Override
    public String toString() {
        return "Vocabulary{" +
                "name='" + name + '\'' +
                ", countWord=" + countWord +
                ", id=" + id +
                '}';
    }
}
