package ru.polis.wordsheap.games;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import ru.polis.wordsheap.R;
import ru.polis.wordsheap.database.DBService;
import ru.polis.wordsheap.games.truefalse.Question;
import ru.polis.wordsheap.objects.Word;

public class TrueFalseActivity extends AbstractGameActivity{
    private View gameView;
    private Question currentQuestion;
    private Random random;
    private int score;
    private int scoreStep;
    private List<Word> allWords;

    private TextView scoreView;
    private TextView multipleView;
    private TextView questionView;
    private TextView answerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        score = 0;
        random = new Random(System.currentTimeMillis());
        scoreStep = 10;
        allWords = DBService.getInstance(getApplicationContext()).getAllWords();
        gameView.findViewById(R.id.btnNegativeTrueFalseGame).setOnClickListener(this);
        gameView.findViewById(R.id.btnPositiveTrueFalseGame).setOnClickListener(this);

        scoreView = (TextView) gameView.findViewById(R.id.gameTrueFalseScoreView);
        multipleView = (TextView) gameView.findViewById(R.id.gameTrueFalseMnozitelView);
        questionView = (TextView) gameView.findViewById(R.id.gameTrueFalseQuestionView);
        answerView = (TextView) gameView.findViewById(R.id.gameTrueFalseAnswerView);

        nextQuestion();
    }

    private void nextQuestion() {
        currentQuestion = getNextQuestion();
        scoreView.setText(getResources().getString(R.string.true_false_score, score));
        multipleView.setText(getResources().getString(R.string.true_false_multiple, scoreStep));
        questionView.setText(currentQuestion.getQuestion());
        answerView.setText(currentQuestion.getAnswer());
    }

    @Override
    protected View getGameView() {
        gameView = getLayoutInflater().inflate(R.layout.view_game_truefalse, null);
        return gameView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnNegativeTrueFalseGame:
                takeAnswer(false);
                break;
            case R.id.btnPositiveTrueFalseGame:
                takeAnswer(true);
                break;
        }
    }

    private void takeAnswer(boolean answer) {
        if(answer == currentQuestion.getResult()){
            addRightAnswer();
        } else {
            addFalseAnswer();
        }
        nextQuestion();
    }

    private Question getNextQuestion() {
        int rndWord = random.nextInt(words.length);
        String question = words[rndWord].getWord();
        int isRight = random.nextInt(2);
        String answer;
        boolean result;
        if(isRight == 1){
            answer = words[rndWord].getTranslate();
            result = true;
        }else{
            answer = allWords.get(random.nextInt(allWords.size())).getTranslate();
            result = false;
            if(answer.equalsIgnoreCase(words[rndWord].getTranslate())){
                result = true;
            }
        }
        return new Question(question, answer, result);
    }

    private void addRightAnswer() {
        score += scoreStep;
        scoreStep *= 1.3;
    }

    private void addFalseAnswer() {
        scoreStep = 10;
    }
}
