package ru.polis.wordsheap;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.polis.wordsheap.adapters.WordsAdapter;
import ru.polis.wordsheap.database.DBService;
import ru.polis.wordsheap.games.TrueFalseActivity;
import ru.polis.wordsheap.objects.Vocabulary;
import ru.polis.wordsheap.objects.Word;

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
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

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

    public static class ListWordFragment extends ListFragment {
        public static final String TAG = "ListWordFragmentLOG";

        private Vocabulary vocabulary;
        private ArrayAdapter<Word> adapter;
        private List<Word> listWords;

        public void setVocabulary(Vocabulary vocabulary) {
            this.vocabulary = vocabulary;
        }

        @Override
        public void onResume() {
            super.onResume();
            listWords = DBService.getInstance(getActivity()).getWordsByVocabulary(vocabulary);
            adapter = new WordsAdapter(getActivity(), R.layout.list_item_word, listWords);
            setListAdapter(adapter);
        }

        public void startLearn() {
            List<Word> listWordToLearn = new ArrayList<>();
            for(Word word : listWords){
                if(word.isActive()) listWordToLearn.add(word);
            }
            if(listWordToLearn.size() > 0){
                Intent intent = new Intent(getActivity(), TrueFalseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("words", listWordToLearn.toArray());
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "Choose word", Toast.LENGTH_SHORT).show();
            }
        }

        public void showAddWordDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_new_word, null);
            final EditText editTextName = (EditText) view.findViewById(R.id.editTextWordName);
            final EditText editTextTranslate = (EditText) view.findViewById(R.id.editTextWordTranslate);
            builder.setView(view)
                    .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = editTextName.getText().toString();
                            String translate = editTextTranslate.getText().toString();
                            if (DBService.getInstance(getActivity()).validateWordData(name, translate)) {
                                long id = DBService.getInstance(getActivity()).addWord(new Word(name, translate), vocabulary);
                                listWords.add(new Word(id, name, translate, vocabulary.getId()));
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.invalid_new_word_value), Toast.LENGTH_SHORT).show();
                                showAddWordDialog();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, null).setTitle(R.string.addMewWordTitle);
            builder.create();
            builder.show();
        }
    }
}
