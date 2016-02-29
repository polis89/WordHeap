package ru.polis.wordsheap.objects;


import java.io.Serializable;

public class Word implements Serializable{
    private int id;
    private String word;
    private String translate;
    private boolean active;
    private int progress;
    private int vocabulary_id;

    public Word(String word, String translate){
        this.word = word;
        this.translate = translate;
        active = true;
        progress = 0;
    }

    public Word(int id, String name, String translate, int vocabulary_id) {
        this(name, translate);
        this.id = id;
        this.vocabulary_id = vocabulary_id;
    }

    public Word(int id, String word, String translate, boolean active, int progress, int vocabulary_id) {
        this.id = id;
        this.word = word;
        this.translate = translate;
        this.active = active;
        this.progress = progress;
        this.vocabulary_id = vocabulary_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getProgress() {
        return progress;
    }

    public void addProgress(int addedProgress) {
        this.progress += addedProgress;
        if (progress > 100) progress = 100;
        if (progress < 0) progress = 0;
    }

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", translate='" + translate + '\'' +
                ", active=" + active +
                ", progress=" + progress +
                ", vocabulary_id=" + vocabulary_id +
                '}';
    }
}
