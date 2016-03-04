package ru.polis.wordsheap.games.truefalse;

public class Question {
    private String question;
    private String answer;
    private boolean result;
    private int idInArray;

    public Question(String question, String answer, boolean result, int id) {
        this.question = question;
        this.answer = answer;
        this.result = result;
        this.idInArray = id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean getResult() {
        return result;
    }

    public int getIdInArray() {
        return idInArray;
    }

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", result=" + result +
                '}';
    }
}
