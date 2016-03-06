package ru.polis.wordsheap.games;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import ru.polis.wordsheap.R;
import ru.polis.wordsheap.database.DBService;
import ru.polis.wordsheap.games.truefalse.Question;
import ru.polis.wordsheap.objects.Word;


public class TrueFalseActivity extends AbstractGameActivity{
    public static final Integer PLAY_TIME = 20;
    public static final Integer RIGHT_ANSWER_PROGRESS = 1;
    public static final Integer FALSE_ANSWER_PROGRESS = -1;

    private View gameView;
    private Question currentQuestion;
    private Random random;
    private int score;
    private int scoreStep;
    private List<Word> allWords;

    private RelativeLayout relativeLayout;
    private TextView scoreView;
    private TextView multipleView;
    private TextView questionView;
    private TextView answerView;
    private TextView chronometerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        score = 0;
        random = new Random(System.currentTimeMillis());
        scoreStep = 10;
        allWords = DBService.getInstance(getApplicationContext()).getAllWords();

        relativeLayout = (RelativeLayout) gameView.findViewById(R.id.trueFalseLayout);
        scoreView = (TextView) gameView.findViewById(R.id.gameTrueFalseScoreView);
        multipleView = (TextView) gameView.findViewById(R.id.gameTrueFalseMnozitelView);
        questionView = (TextView) gameView.findViewById(R.id.gameTrueFalseQuestionView);
        answerView = (TextView) gameView.findViewById(R.id.gameTrueFalseAnswerView);
        chronometerView = (TextView) gameView.findViewById(R.id.chronometer);

        questionView.setText(R.string.true_false_start);
        answerView.setText("");
        scoreView.setText(getResources().getString(R.string.true_false_score, score));
        multipleView.setText(getResources().getString(R.string.true_false_multiple, scoreStep));
        chronometerView.setText(PLAY_TIME.toString());

        relativeLayout.setOnClickListener(this);
    }

    //Called after user touch screen to start game
    private void startGame() {
        relativeLayout.setOnClickListener(null); //Delete Listener from Layout
        gameView.findViewById(R.id.btnNegativeTrueFalseGame).setOnClickListener(this);
        gameView.findViewById(R.id.btnPositiveTrueFalseGame).setOnClickListener(this);

        TimerAsyncTask time = new TimerAsyncTask(PLAY_TIME);
        time.execute();

        nextQuestion();
    }

    //Called after Time limit
    private void stopGame() {
        showResults();
    }

    //Called after answer or by startGame
    private void nextQuestion() {
        currentQuestion = getNextQuestion();
        scoreView.setText(getResources().getString(R.string.true_false_score, score));
        multipleView.setText(getResources().getString(R.string.true_false_multiple, scoreStep));
        questionView.setText(currentQuestion.getQuestion());
        answerView.setText(currentQuestion.getAnswer());
    }

    //Generate random question
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
            result = answer.equalsIgnoreCase(words[rndWord].getTranslate());
        }
        return new Question(question, answer, result, rndWord);
    }

    // Check an Answer
    private void takeAnswer(boolean answer) {
        if(answer == currentQuestion.getResult()){
            addRightAnswer();
        } else {
            addFalseAnswer();
        }
        nextQuestion();
    }

    //Called after true answer
    private void addRightAnswer() {
        score += scoreStep;
        scoreStep *= 1.3;
        words[currentQuestion.getIdInArray()].addProgress(RIGHT_ANSWER_PROGRESS);
    }

    //Called after false answer
    private void addFalseAnswer() {
        scoreStep = 10;
        words[currentQuestion.getIdInArray()].addProgress(FALSE_ANSWER_PROGRESS);
    }

    //To show results
    private void showResults() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.results);
        builder.setMessage(getResources().getString(R.string.result_true_false, score));
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                endOfGame();
            }
        });
        builder.create().show();
    }

    //Method from Parent class, to add game View in Activity with ToolBar
    @Override
    protected View getGameView() {
        gameView = getLayoutInflater().inflate(R.layout.view_game_truefalse, null);
        return gameView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.trueFalseLayout:
                startGame();
                break;
            case R.id.btnNegativeTrueFalseGame:
                takeAnswer(false);
                break;
            case R.id.btnPositiveTrueFalseGame:
                takeAnswer(true);
                break;
        }
    }

    //Class for timer run
    private class TimerAsyncTask extends AsyncTask<Void, Integer, Void>{
        private int time;

        public TimerAsyncTask(int time) {
            this.time = time;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (time > 0) {
                SystemClock.sleep(1000);
                time--;
                publishProgress(time);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            chronometerView.setText(values[0].toString());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            stopGame();
        }
    }
}
