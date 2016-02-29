package ru.polis.wordsheap;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import ru.polis.wordsheap.adapters.VocabularyAdapter;
import ru.polis.wordsheap.database.DBService;
import ru.polis.wordsheap.objects.Vocabulary;

public class ListVocabularyFragment extends ListFragment{
    public static final String TAG = "ListVocabularyFragmentLOG";
    private ArrayAdapter<Vocabulary> adapter;
    private List<Vocabulary> listVocabularys;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "--onCreate--");

        listVocabularys = DBService.getInstance(getActivity()).getAllVocabularys();
        if(!(listVocabularys.size() > 0)) {
            DBService.getInstance(getActivity()).addTestedValues();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "--onResume--");
        listVocabularys = DBService.getInstance(getActivity()).getAllVocabularys();
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
                            long id = DBService.getInstance(getActivity()).addVocabulary(new Vocabulary(name));
                            listVocabularys.add(new Vocabulary((int) id, name));
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
