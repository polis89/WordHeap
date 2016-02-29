package ru.polis.wordsheap.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.polis.wordsheap.R;
import ru.polis.wordsheap.WordsActivity;
import ru.polis.wordsheap.database.DBService;
import ru.polis.wordsheap.objects.Word;

public class WordsAdapter extends ArrayAdapter<Word> implements View.OnClickListener {
    private final Context context;
    private final List<Word> wordList;

    public WordsAdapter(Context context, int resource, List<Word> objects) {
        super(context, resource, objects);
        this.context = context;
        this.wordList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.list_item_word, parent, false);

        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.inner_relative_layout_word);
        ImageView imgSettings = (ImageView) view.findViewById(R.id.settingsWordView);
        TextView nameTextView = (TextView) view.findViewById(R.id.wordName);
        TextView translateTextView = (TextView) view.findViewById(R.id.wordTranslate);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox_list_item);

        Word word = wordList.get(position);

        nameTextView.setText(word.getWord());
        translateTextView.setText(word.getTranslate());
        progressBar.setProgress(word.getProgress());
        checkBox.setChecked(word.isActive());
        checkBox.setClickable(false);

        relativeLayout.setTag(position);
        relativeLayout.setOnClickListener(this);

        imgSettings.setTag(position);
        imgSettings.setOnClickListener(this);

        if(!word.isActive()){
            view.setBackgroundResource(R.color.colorWordNotActive);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.inner_relative_layout_word:
                Word word = wordList.get((Integer) v.getTag());
                DBService.getInstance(getContext()).changeWordActive(word);
                word.setActive(!word.isActive());
                notifyDataSetChanged();
                break;
            case R.id.settingsWordView:
                showSettingsDialog(wordList.get((Integer) v.getTag()));
                break;
        }
    }

    private void showSettingsDialog(Word word) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        SettingsListener settingsListener = new SettingsListener(word);
        builder.setTitle(R.string.settings_title);
        builder.setItems(R.array.settings_word_items, settingsListener);
        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
    }

    private class SettingsListener implements DialogInterface.OnClickListener{
        private Word word;

        public SettingsListener(Word word) {
            this.word = word;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case 0:
                    showChangeWordDialog();
                    return;
                case 1:
                    DBService.getInstance(getContext()).resetWordProgress(word);
                    notifyDataSetChanged();
                    return;
                case 2:
                    showDeleteDialog();
                    return;
            }
        }

        private void showChangeWordDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = ((WordsActivity)context).getLayoutInflater().inflate(R.layout.dialog_word_change, null);
            final EditText etName = (EditText) view.findViewById(R.id.editTextWordName);
            final EditText etTranslate = (EditText) view.findViewById(R.id.editTextWordTranslate);
            etName.setText(word.getWord());
            etTranslate.setText(word.getTranslate());
            builder.setView(view);
            builder.setMessage(R.string.word_change_dialog_msg)
                    .setPositiveButton(R.string.change_word_btn_txt, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = etName.getText().toString();
                            String translate = etTranslate.getText().toString();
                            if (!DBService.getInstance(getContext()).updateWord(word, name, translate)) {
                                Toast.makeText(context, R.string.validate_word_failed, Toast.LENGTH_SHORT).show();
                                showChangeWordDialog();
                            } else {
                                word.setWord(name);
                                word.setTranslate(translate);
                                notifyDataSetChanged();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, null);
            builder.create().show();
        }

        private void showDeleteDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getResources().getString(R.string.word_delete_accept, word.getWord()))
                    .setPositiveButton(R.string.delete_btn_txt, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DBService.getInstance(getContext()).deleteWord(word);
                            wordList.remove(word);
                            notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null);
            builder.create().show();
        }
    }
}
