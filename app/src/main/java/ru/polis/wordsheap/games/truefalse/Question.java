package ru.polis.wordsheap.games.truefalse;

public class Question {
    private String question;
    private String answer;
    private boolean result;

    public Question(String question, String answer, boolean result) {
        this.question = question;
        this.answer = answer;
        this.result = result;
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

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", result=" + result +
                '}';
    }
}
