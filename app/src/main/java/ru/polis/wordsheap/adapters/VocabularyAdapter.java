package ru.polis.wordsheap.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.polis.wordsheap.MainActivity;
import ru.polis.wordsheap.R;
import ru.polis.wordsheap.WordsActivity;
import ru.polis.wordsheap.database.DBService;
import ru.polis.wordsheap.objects.Vocabulary;

public class VocabularyAdapter extends ArrayAdapter<Vocabulary> implements View.OnClickListener {
    private final Context context;
    private List<Vocabulary> vocabularyList;

    public VocabularyAdapter(Context context, int resource, List<Vocabulary> objects) {
        super(context, resource, objects);
        this.context = context;
        this.vocabularyList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.list_item_vocabulary, parent, false);

        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.inner_relative_layout);
        ImageView imgSettings = (ImageView) view.findViewById(R.id.settingsView);
        TextView nameTextView = (TextView) view.findViewById(R.id.firstLine);
        TextView countWordsTextView = (TextView) view.findViewById(R.id.secondLine);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        Vocabulary vocabulary = vocabularyList.get(position);

        nameTextView.setText(vocabulary.getName());
        countWordsTextView.setText(context.getResources().getString(R.string.count_word_text, vocabulary.getCountWord()));
        progressBar.setProgress(vocabulary.getProgressVocabulary());

        relativeLayout.setTag(position);
        relativeLayout.setOnClickListener(this);

        imgSettings.setTag(position);
        imgSettings.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.inner_relative_layout:
                Intent wordsIntent = new Intent(context, WordsActivity.class);
                wordsIntent.putExtra("Vocabulary ID", vocabularyList.get((Integer) v.getTag()));
                context.startActivity(wordsIntent);
                break;
            case R.id.settingsView:
                showSettingsDialog(vocabularyList.get((Integer) v.getTag()));
                break;
        }
    }

    private void showSettingsDialog(Vocabulary vocabulary) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        SettingsListener settingsListener = new SettingsListener(vocabulary);
        builder.setTitle(R.string.settings_title);
        builder.setItems(R.array.settings_vocabular_items, settingsListener);
        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
    }

    private class SettingsListener implements DialogInterface.OnClickListener {
        private Vocabulary vocabulary;

        public SettingsListener(Vocabulary vocabulary) {
            this.vocabulary = vocabulary;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case 0:
                    showRenameDialog();
                    return;
                case 1:
                    DBService.getInstance(getContext()).resetVocabularyProgress(vocabulary);
                    vocabularyList = DBService.getInstance(getContext()).getAllVocabularys();
                    notifyDataSetChanged();
                    return;
                case 2:
                    showDeleteDialog();
                    return;
            }
        }

        private void showDeleteDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getResources().getString(R.string.voc_delete_accept, vocabulary.getName()))
                    .setPositiveButton(R.string.delete_btn_txt, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DBService.getInstance(context).deleteVocabulary(vocabulary);
                            vocabularyList.remove(vocabulary);
                            notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null);
            builder.create().show();
        }

        private void showRenameDialog() {
            View view = ((MainActivity)context).getLayoutInflater().inflate(R.layout.dialog_rename_voc, null);
            final EditText newName = (EditText) view.findViewById(R.id.editTextVocabularyRename);
            AlertDialog.Builder build = new AlertDialog.Builder(context);
            build.setView(view)
                    .setTitle(getContext().getResources().getString(R.string.rename_vocabulary_message, vocabulary.getName()))
                    .setPositiveButton(R.string.rename_btn_txt, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newNameString = newName.getText().toString();
                            if(!DBService.getInstance(context).renameVocabulary(vocabulary, newNameString)){
                                Toast.makeText(context, R.string.validate_vocabulary_name_failed, Toast.LENGTH_SHORT).show();
                                showRenameDialog();
                            }else {
                                vocabulary.setName(newNameString);
                                notifyDataSetChanged();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, null);
            build.create().show();
        }
    }
}
