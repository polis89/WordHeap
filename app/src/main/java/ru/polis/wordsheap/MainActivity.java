package ru.polis.wordsheap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivityLOG";
    private ListVocabularyFragment vocabularyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.vocabularyToolBar);
        toolbar.setTitle(R.string.vocabulary_activity_title);
        setSupportActionBar(toolbar);

        vocabularyFragment = (ListVocabularyFragment) getFragmentManager().findFragmentById(R.id.fragmentVocabulary);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vocabulary_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_new_vocabulary:
                vocabularyFragment.showAddVocabularyDialog();
                return true;
            default:
                return false;
        }
    }

    //    @Override
//    public void onClick(View v) {
//        if(v.equals(btnAdd)){
//            Log.i(TAG, "-- button new dictionary pressed --");
//            showAddVocabularyDialog();
//        }
//    }

}
