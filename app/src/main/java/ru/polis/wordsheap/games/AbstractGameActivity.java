package ru.polis.wordsheap.games;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import ru.polis.wordsheap.R;
import ru.polis.wordsheap.database.DBService;
import ru.polis.wordsheap.objects.Word;

public abstract class AbstractGameActivity  extends AppCompatActivity implements View.OnClickListener {
    protected Word[] words;
    public static final String TAG = "GameActivityLOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Object[] wordsToLearn = (Object[]) bundle.getSerializable("words");
        words = new Word[wordsToLearn.length];
        for(int i = 0; i < wordsToLearn.length; i++){
            words[i] = (Word) wordsToLearn[i];
        }

        setContentView(R.layout.activity_abstract_game);
        LinearLayout gameLayout = (LinearLayout)findViewById(R.id.abstract_game_layout);

        Toolbar toolbar = (Toolbar)findViewById(R.id.gameToolBar);
        toolbar.setTitle(R.string.abstract_game_title);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        View gameView = getGameView();
        gameLayout.addView(gameView);

    }

    protected abstract View getGameView();

    protected void endOfGame() {
        DBService dbService = DBService.getInstance(this);
        dbService.updateWordsProgress(words);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
