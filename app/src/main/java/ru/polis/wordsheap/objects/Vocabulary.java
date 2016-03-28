package ru.polis.wordsheap.objects;

import java.io.Serializable;

import ru.polis.wordsheap.enums.Language;

public class Vocabulary implements Serializable{
    public static final String TAG = "VocabularyLog";

    private long id;
    private String name;
    private int countWord;
    private Language language;
    private int progressVocabulary;

    public Vocabulary(String name, Language language){
        this.name = name;
        this.language = language;
        countWord = 0;
        progressVocabulary = 0;
    }

    public Vocabulary(long id, String name, Language language) {
        this(name, language);
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCountWord(int countWord) {
        this.countWord = countWord;
    }

    public int getCountWord() {
        return countWord;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setProgressVocabulary(int progressVocabulary) {
        this.progressVocabulary = progressVocabulary;
    }

    public int getProgressVocabulary() {
        return progressVocabulary;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Language getLanguage() {
        return language;
    }

    @Override
    public String toString() {
        return "Vocabulary{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", countWord=" + countWord +
                ", language=" + language +
                ", progressVocabulary=" + progressVocabulary +
                '}';
    }
}
