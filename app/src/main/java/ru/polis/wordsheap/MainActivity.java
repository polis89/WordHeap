package ru.polis.wordsheap;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;

import ru.polis.wordsheap.adapters.VocabularyAdapter;
import ru.polis.wordsheap.database.DBService;
import ru.polis.wordsheap.enums.Language;
import ru.polis.wordsheap.objects.Vocabulary;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivityLOG";
    private static final String PREFERENCE = "preference";
    private static final String LANGUAGE_PREFERENCE = "language";
    private ListVocabularyFragment vocabularyFragment;
    public Language language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Define language
        SharedPreferences savedPreference = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        language = Language.valueOf(savedPreference.getString(LANGUAGE_PREFERENCE, "ENG_RUS"));
        Toast.makeText(getApplicationContext(), "language: " + language.toString(), Toast.LENGTH_SHORT).show();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.vocabularyToolBar);
        toolbar.setTitle(R.string.vocabulary_activity_title);
        setSupportActionBar(toolbar);

        vocabularyFragment = (ListVocabularyFragment) getFragmentManager().findFragmentById(R.id.fragmentVocabulary);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vocabulary_activity_menu, menu);
        MenuItem languageMenuItem = menu.getItem(1);
        languageMenuItem.setIcon(getLanguageIconID(language));
        return true;
    }

    //Get Language Icon from drawable
    private int getLanguageIconID(Language language) {
        switch (language){
            case DE_RUS:
                return R.drawable.de_rus;
            case DE_ENG:
                return R.drawable.de_eng;
            case ENG_RUS:
                return R.drawable.eng_rus;
            case ENG_DE:
                return R.drawable.eng_de;
            case RUS_ENG:
                return R.drawable.rus_eng;
            case RUS_DE:
                return R.drawable.rus_de;
            default:
                Log.e(TAG, "No such Language");
                // TODO: 18.03.2016 Add empty icon
                return R.drawable.eng_rus;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_new_vocabulary:
                vocabularyFragment.showAddVocabularyDialog();
                return true;
            case R.id.choose_language:
                showChooseLanguageDialog();
                return true;
            default:
                return false;
        }
    }

    private void showChooseLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater =this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_choose_language, null);
        final RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.chooseLanguageRadioGroup);
        int chekedID = 0;
        for(Language lan : Language.values()){
            RadioButton radioButtonView = new RadioButton(this);
            radioButtonView.setCompoundDrawablesRelativeWithIntrinsicBounds(getLanguageIconID(lan), 0, 0, 0);
            radioGroup.addView(radioButtonView);
            radioButtonView.setPadding(0, 15, 0, 15);
            radioButtonView.setCompoundDrawablePadding(25);
            radioButtonView.setText(getResources().getString(lan.getDescriptionID()));
            if(language.equals(lan)) {
                chekedID = radioGroup.getChildCount(); //Check aktiv Language
//                Toast.makeText(getApplicationContext(), "checked: " + chekedID, Toast.LENGTH_SHORT).show();
            }
        }
        radioGroup.check(chekedID);
        builder.setView(dialogView)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int id = (radioGroup.getCheckedRadioButtonId() - 1) % Language.values().length;
                        Toast.makeText(getApplicationContext(), "checked: " + id, Toast.LENGTH_SHORT).show();
                        Language newLanguage = Language.values()[id];
                        if(!newLanguage.equals(language)) {
                            SharedPreferences preference = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
                            SharedPreferences.Editor editor = preference.edit();
                            editor.putString(LANGUAGE_PREFERENCE, newLanguage.toString());
                            editor.apply();
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setTitle(R.string.chooseLanguageTitle);
        builder.create();
        builder.show();
    }

    public static class ListVocabularyFragment extends ListFragment {
        public static final String TAG = "ListVocabularyFragLOG";
        private ArrayAdapter<Vocabulary> adapter;
        private List<Vocabulary> listVocabularys;

        @Override
        public void onResume() {
            super.onResume();
            Log.i(TAG, "--onResume--");

            //Check number of Vocabularys (if 0 -> need to add Values)
            if(DBService.getInstance(getActivity()).getAllVocabularys().size() == 0) {
                DBService.getInstance(getActivity()).addStartValues(this.getResources().getAssets());
            }
            listVocabularys = DBService.getInstance(getActivity()).getAllVocabularysByLanguage(((MainActivity)getActivity()).language);

            adapter = new VocabularyAdapter(getActivity(), R.layout.list_item_vocabulary, listVocabularys);
            setListAdapter(adapter);
        }

        public void showAddVocabularyDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_new_voc, null);
            final EditText editText = (EditText) view.findViewById(R.id.editTextVocabularyName);
            builder.setView(view)
                    .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = editText.getText().toString();
                            if(DBService.getInstance(getActivity()).validateVocabularyName(name)){
                                long id = DBService.getInstance(getActivity()).addVocabulary(new Vocabulary(name, ((MainActivity)getActivity()).language));
                                listVocabularys.add(new Vocabulary((int) id, name, Language.DE_RUS));
                                adapter.notifyDataSetChanged();
                            }else{
                                Toast.makeText(getActivity(), getString(R.string.invalid_new_word_value), Toast.LENGTH_SHORT).show();
                                showAddVocabularyDialog();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, null).setTitle(R.string.addMewVocabularyTitle);
            builder.create();
            builder.show();
        }
    }
}
