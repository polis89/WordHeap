package ru.polis.wordsheap.games.truefalse;

import java.util.List;
import java.util.Random;

import ru.polis.wordsheap.objects.Word;

public class Game {
    private Random random;
    private List<Word> listQuestions;
    private List<Word> listAnswers;
    private int score;
    private int scoreStep;

    public Game(List<Word> listQuestions, List<Word> listAnswer) {
        this.listQuestions = listQuestions;
        this.listAnswers = listAnswer;
        score = 0;
        random = new Random(System.currentTimeMillis());
        scoreStep = 10;
    }

    public Question getNextQuestion() {
        int rndWord = random.nextInt(listQuestions.size());
        String question = listQuestions.get(rndWord).getWord();
        int isRight = random.nextInt(2);
        String answer;
        boolean rest;
        if(isRight == 1){
            answer = listQuestions.get(rndWord).getTranslate();
            rest = true;
        }else{
            answer = listAnswers.get(random.nextInt(listAnswers.size())).getTranslate();
            rest = false;
            if(answer.equalsIgnoreCase(listQuestions.get(rndWord).getTranslate())){
                rest = true;
            }
        }
        return new Question(question, answer, rest);
    }

    public void addRightAnswer() {
        score +=scoreStep;
        scoreStep *= 1.3;
    }

    public void addFalseAnswer() {
        scoreStep = 10;
    }

    public String getScore() {
        return String.valueOf(score);
    }

    public int getScoreStep() {
        return scoreStep;
    }
}
