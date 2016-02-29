package ru.polis.wordsheap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import ru.polis.wordsheap.objects.Vocabulary;

public class WordsActivity extends AppCompatActivity{
    public static final String TAG = "WordsActivityLOG";
    private ListWordFragment wordFragment;
    private Vocabulary vocabulary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "--onCreate--");
        if(savedInstanceState == null) Log.w(TAG, "--no Bundle--");
        setContentView(R.layout.activity_words);

        Toolbar toolbar = (Toolbar)findViewById(R.id.wordToolBar);
        toolbar.setTitle(R.string.word_activity_title);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent inputIntent = getIntent();
        vocabulary = (Vocabulary) inputIntent.getSerializableExtra("Vocabulary ID");
        if(vocabulary == null) Log.w(TAG, "--no Vocabulary--");

        wordFragment = (ListWordFragment) getFragmentManager().findFragmentById(R.id.fragmentWord);
        wordFragment.setVocabulary(vocabulary);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.word_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_new_word:
                wordFragment.showAddWordDialog();
                return true;
            case R.id.action_start:
                wordFragment.startLearn();
                return true;
            default:
                return false;
        }
    }
}
