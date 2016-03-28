package ru.polis.wordsheap.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ru.polis.wordsheap.enums.Language;
import ru.polis.wordsheap.objects.Vocabulary;
import ru.polis.wordsheap.objects.Word;

public class DBService {
    public static final String VOCABULARYS_FILE_PATH = "vocabularys"; //in assets
    public static final String TAG = "db_service_log";

    public static final String TABLE_LANGUAGE_NAME = "language";
    public static final String TABLE_LANGUAGE_KEY_ID = "_id";
    public static final String TABLE_LANGUAGE_KEY_NAME = "language";

    public static final String TABLE_VOCABULARY_NAME = "vocabulary";
    public static final String TABLE_VOCABULARY_KEY_ID = "_id";
    public static final String TABLE_VOCABULARY_KEY_NAME = "vocabulary";
    public static final String TABLE_VOCABULARY_KEY_LANGUAGE_ID = "vocabulary_id";

    public static final String TABLE_WORD_NAME = "word";
    public static final String TABLE_WORD_KEY_ID = "_id";
    public static final String TABLE_WORD_KEY_NAME = "word";
    public static final String TABLE_WORD_KEY_TRANSLATE = "translate";
    public static final String TABLE_WORD_KEY_PROGRESS = "progress";
    public static final String TABLE_WORD_KEY_ACTIVE = "active";
    public static final String TABLE_WORD_KEY_VOCABULARY_ID = "vocabulary_id";

    private static DBService dbService;
    private DBHelper dbHelper;

    private DBService(Context context){
        dbHelper = new DBHelper(context);
    }

    public static DBService getInstance(Context context){
        if(dbService == null){
            dbService = new DBService(context);
        }
        return dbService;
    }

    private long addLanguage(Language language){
        String name = language.toString();
        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_LANGUAGE_KEY_NAME, name);
        long rest = dataBase.insert(TABLE_LANGUAGE_NAME, null, contentValues);
        dbHelper.close();
        return rest;
    }

    private Language getLanguageByID(int id) {
        SQLiteDatabase dataBase = dbHelper.getReadableDatabase();
        Cursor cursor = dataBase.query(TABLE_LANGUAGE_NAME, null, TABLE_LANGUAGE_KEY_ID + " = ? ", new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        int id_d = cursor.getInt(cursor.getColumnIndex(TABLE_LANGUAGE_KEY_ID));
        cursor.close();
        dbHelper.close();
        for(Language l : Language.values()){
            if (getLanguageID(l) == id_d){
                return l;
            }
        }
        return null;
    }

    private int getLanguageID(Language language) {
        SQLiteDatabase dataBase = dbHelper.getReadableDatabase();
        Cursor cursor = dataBase.query(TABLE_LANGUAGE_NAME, null, TABLE_LANGUAGE_KEY_NAME + " = ? ", new String[]{language.toString()}, null, null, null);
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex(TABLE_LANGUAGE_KEY_ID));
        cursor.close();
        dbHelper.close();
        return id;
    }

    public int getLanguagesCount(){
        Log.i(TAG, "--- getLanguagesCount ---");
        SQLiteDatabase dataBase = dbHelper.getReadableDatabase();
        Cursor cursor = dataBase.query(TABLE_LANGUAGE_NAME, null, null, null, null, null, null);
        int count = 0;
        if(cursor.moveToFirst()){
            do{
                count++;
            }while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return count;
    }

    public long addVocabulary(Vocabulary vocabulary){
        Log.i(TAG, "--- addVocabulary /" + vocabulary.getName() + "/ ---");
        String name = vocabulary.getName();
        int language_id = this.getLanguageID(vocabulary.getLanguage());
        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_VOCABULARY_KEY_NAME, name);
        contentValues.put(TABLE_VOCABULARY_KEY_LANGUAGE_ID, language_id);
        long rest = dataBase.insert(TABLE_VOCABULARY_NAME, null, contentValues);
        dbHelper.close();
        return rest;
    }

    public List<Vocabulary> getAllVocabularys(){
        Log.i(TAG, "--- getAllVocabularys ---");
        List<Vocabulary> list = new ArrayList<>();
        SQLiteDatabase dataBase = dbHelper.getReadableDatabase();
        Cursor cursor = dataBase.query(TABLE_VOCABULARY_NAME, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            int column_id_index = cursor.getColumnIndex(TABLE_VOCABULARY_KEY_ID);
            int column_name_index = cursor.getColumnIndex(TABLE_VOCABULARY_KEY_NAME);
            int column_languauge_id_index = cursor.getColumnIndex(TABLE_VOCABULARY_KEY_LANGUAGE_ID);
            Language language = this.getLanguageByID(cursor.getInt(column_languauge_id_index));
            do{
                Vocabulary vocabulary = new Vocabulary(
                                cursor.getInt(column_id_index),
                                cursor.getString(column_name_index),
                                language);
                List<Word> listWords = getWordsByVocabulary(vocabulary);
                vocabulary.setCountWord(listWords.size());
                vocabulary.setProgressVocabulary(getVocabularyProgressByListOfWords(listWords));
                list.add(vocabulary);
            }while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return list;
    }

    public List<Vocabulary> getAllVocabularysByLanguage(Language language){
        Log.i(TAG, "--- getAllVocabularys ---");
        List<Vocabulary> list = new ArrayList<>();
        int lang_id = getLanguageID(language);
        SQLiteDatabase dataBase = dbHelper.getReadableDatabase();
        Cursor cursor = dataBase.query(
                TABLE_VOCABULARY_NAME,
                null,
                TABLE_VOCABULARY_KEY_LANGUAGE_ID + " = ? ",
                new String[]{String.valueOf(lang_id)},
                null,
                null,
                null);
        if(cursor.moveToFirst()){
            int column_id_index = cursor.getColumnIndex(TABLE_VOCABULARY_KEY_ID);
            int column_name_index = cursor.getColumnIndex(TABLE_VOCABULARY_KEY_NAME);
            do{
                Vocabulary vocabulary = new Vocabulary(
                                cursor.getInt(column_id_index),
                                cursor.getString(column_name_index),
                                language);
                List<Word> listWords = getWordsByVocabulary(vocabulary);
                vocabulary.setCountWord(listWords.size());
                vocabulary.setProgressVocabulary(getVocabularyProgressByListOfWords(listWords));
                list.add(vocabulary);
            }while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return list;
    }

    private int getVocabularyProgressByListOfWords(List<Word> listWords) {
        Log.i(TAG, "--- getVocabularyProgressByListOfWords ---");
        int summProgress = 0;
        for(Word word : listWords){
            summProgress += word.getProgress();
        }
        if(listWords.size() != 0){
//            Log.i(TAG, "--- getVocabularyProgressByListOfWords; return = " + summProgress / listWords.size() + " ---");
            return summProgress / listWords.size();
        }else{
//            Log.i(TAG, "--- getVocabularyProgressByListOfWords; standart return ---");
            return 0;
        }
    }

    public Vocabulary getVocabularyByID(long id){
        SQLiteDatabase dataBase = dbHelper.getReadableDatabase();
        Cursor cursor = dataBase.query(
                TABLE_VOCABULARY_NAME,
                null,
                TABLE_VOCABULARY_KEY_ID + " = ? ",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
        if(cursor.moveToFirst()) {
            long id_d = cursor.getLong(cursor.getColumnIndex(TABLE_VOCABULARY_KEY_ID));
            String name = cursor.getString(cursor.getColumnIndex(TABLE_VOCABULARY_KEY_NAME));
            Language language = getLanguageByID(cursor.getInt(cursor.getColumnIndex(TABLE_VOCABULARY_KEY_LANGUAGE_ID)));
            cursor.close();
            dbHelper.close();
            return new Vocabulary(id_d, name, language);
        } else {
            cursor.close();
            dbHelper.close();
            return null;
        }
    }

    public boolean renameVocabulary(Vocabulary vocabulary, String newName) {
        if(!validateVocabularyName(newName)) return false;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_VOCABULARY_KEY_NAME, newName);
        database.update(TABLE_VOCABULARY_NAME, contentValues, TABLE_VOCABULARY_KEY_ID + " = ?", new String[]{String.valueOf(vocabulary.getId())});
        dbHelper.close();
        return true;
    }

    public void resetVocabularyProgress(Vocabulary vocabulary) {
        List<Word> words = getWordsByVocabulary(vocabulary);
        for(Word word : words){
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TABLE_WORD_KEY_PROGRESS, 0);
            database.update(TABLE_WORD_NAME, contentValues, TABLE_WORD_KEY_ID + " = ?", new String[]{String.valueOf(word.getId())});
        }
        dbHelper.close();
    }

    public void deleteVocabulary(Vocabulary vocabulary) {
        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
        dataBase.delete(TABLE_VOCABULARY_NAME, TABLE_VOCABULARY_KEY_ID + " = ? ", new String[]{String.valueOf(vocabulary.getId())});
        dataBase.delete(TABLE_WORD_NAME, TABLE_WORD_KEY_VOCABULARY_ID + " = ? ", new String[]{String.valueOf(vocabulary.getId())});
        dbHelper.close();
    }

    public long addWord(Word word, Vocabulary vocabulary){
        String name = word.getWord();
        String translate = word.getTranslate();
        int active = word.isActive() ? 1 : 0;
        int progress = word.getProgress();
        long voc_id = vocabulary.getId();
        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_WORD_KEY_NAME, name);
        contentValues.put(TABLE_WORD_KEY_TRANSLATE, translate);
        contentValues.put(TABLE_WORD_KEY_ACTIVE, active);
        contentValues.put(TABLE_WORD_KEY_PROGRESS, progress);
        contentValues.put(TABLE_WORD_KEY_VOCABULARY_ID, voc_id);
        long rest = dataBase.insert(TABLE_WORD_NAME, null, contentValues);
        dbHelper.close();
        return rest;
    }

    public List<Word> getAllWords(){
        List<Word> list = new ArrayList<>();
        SQLiteDatabase dataBase = dbHelper.getReadableDatabase();
        Cursor cursor = dataBase.query(TABLE_WORD_NAME, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            int column_id_index = cursor.getColumnIndex(TABLE_WORD_KEY_ID);
            int column_name_index = cursor.getColumnIndex(TABLE_WORD_KEY_NAME);
            int column_translate_index = cursor.getColumnIndex(TABLE_WORD_KEY_TRANSLATE);
            int column_active_index = cursor.getColumnIndex(TABLE_WORD_KEY_ACTIVE);
            int column_progress_index = cursor.getColumnIndex(TABLE_WORD_KEY_PROGRESS);
            int column_vocabulary_id_index = cursor.getColumnIndex(TABLE_WORD_KEY_VOCABULARY_ID);
            do{
                Word word = new Word(cursor.getInt(column_id_index),
                        cursor.getString(column_name_index),
                        cursor.getString(column_translate_index),
                        cursor.getInt(column_active_index) == 1,
                        cursor.getInt(column_progress_index),
                        cursor.getInt(column_vocabulary_id_index));
                list.add(word);
            }while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return list;
    }

    public List<Word> getWordsByVocabulary(Vocabulary vocabulary){
        Log.i(TAG, "--- getWordsByVocabulary: " + vocabulary.getName() + " ---");
        List<Word> list = new ArrayList<>();
        SQLiteDatabase dataBase = dbHelper.getReadableDatabase();
        String[] selectionValues = new String[]{String.valueOf(vocabulary.getId())};
        Cursor cursor = dataBase.query(TABLE_WORD_NAME, null, TABLE_WORD_KEY_VOCABULARY_ID + " = ? ", selectionValues, null, null, null);
        if(cursor.moveToFirst()){
            int column_id_index = cursor.getColumnIndex(TABLE_WORD_KEY_ID);
            int column_name_index = cursor.getColumnIndex(TABLE_WORD_KEY_NAME);
            int column_translate_index = cursor.getColumnIndex(TABLE_WORD_KEY_TRANSLATE);
            int column_active_index = cursor.getColumnIndex(TABLE_WORD_KEY_ACTIVE);
            int column_progress_index = cursor.getColumnIndex(TABLE_WORD_KEY_PROGRESS);
            int column_vocabulary_id_index = cursor.getColumnIndex(TABLE_WORD_KEY_VOCABULARY_ID);
            do{
                Word word = new Word(cursor.getInt(column_id_index),
                        cursor.getString(column_name_index),
                        cursor.getString(column_translate_index),
                        cursor.getInt(column_active_index) == 1,
                        cursor.getInt(column_progress_index),
                        cursor.getInt(column_vocabulary_id_index));
                list.add(word);
            }while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return list;
    }

//    private int getCountWordsByVocabulary(Vocabulary vocabulary) {
//        SQLiteDatabase dataBase = dbHelper.getReadableDatabase();
//        String[] columns = new String[] {"count(" + TABLE_WORD_KEY_ID + ") as count"};
//        Cursor cursor = dataBase.query(TABLE_WORD_NAME, columns, TABLE_WORD_KEY_VOCABULARY_ID + " = ? ", new String[]{String.valueOf(vocabulary.getId())}, null, null, null);
//        cursor.moveToFirst();
//        int column_count_index = cursor.getColumnIndex("count");
//        return cursor.getInt(column_count_index);
//    }

    public void deleteWord(Word word){
        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
        dataBase.delete(TABLE_WORD_NAME, TABLE_WORD_KEY_ID + " = ? ", new String[]{String.valueOf(word.getId())});
        dbHelper.close();
    }

    public boolean updateWord(Word word, String name, String translate) {
        if(!validateWordData(name, translate)) return false;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_WORD_KEY_NAME, name);
        contentValues.put(TABLE_WORD_KEY_TRANSLATE, translate);
        database.update(TABLE_WORD_NAME, contentValues, TABLE_WORD_KEY_ID + " = ?", new String[]{String.valueOf(word.getId())});
        dbHelper.close();
        return true;
    }

    public void changeWordActive(Word word) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_WORD_KEY_ACTIVE, !(word.isActive()));
        database.update(TABLE_WORD_NAME, contentValues, TABLE_WORD_KEY_ID + " = ?", new String[]{String.valueOf(word.getId())});
        dbHelper.close();
    }

    public void updateWordsProgress(Word[] words) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        for(Word word : words){
            ContentValues contentValues = new ContentValues();
            contentValues.put(TABLE_WORD_KEY_PROGRESS, word.getProgress());
            database.update(TABLE_WORD_NAME, contentValues, TABLE_WORD_KEY_ID + " = ?", new String[]{String.valueOf(word.getId())});
        }
        dbHelper.close();
    }

    public void resetWordProgress(Word word) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_WORD_KEY_PROGRESS, 0);
        database.update(TABLE_WORD_NAME, contentValues, TABLE_WORD_KEY_ID + " = ?", new String[]{String.valueOf(word.getId())});
        dbHelper.close();
    }

    public boolean validateVocabularyName(String name) {
        return !name.equals("");
    }

    public boolean validateWordData(String name, String translate) {
        return !(name.equals("") || translate.equals(""));
    }

    public void addStartValues(AssetManager assets){
        Log.i(TAG, "--- addStartValues ---");
        if(this.getLanguagesCount() == 0) {
            this.addLanguagesFromEnum();
        }

        try {
            for(String s : assets.list(VOCABULARYS_FILE_PATH)){
                Log.i(TAG, "Assets: " + s);
                InputStream openIS = assets.open(VOCABULARYS_FILE_PATH + "/" + s);
                XMLParser xmlParser = new XMLParser(openIS);
                openIS.close();
                Log.i(TAG, "fileIsVocXML: " + xmlParser.fileIsVocabularyXML());
                if(xmlParser.fileIsVocabularyXML()) {
                    Vocabulary newVoc = xmlParser.getVocabulary();
                    long newVocID = this.addVocabulary(newVoc);
                    newVoc = this.getVocabularyByID(newVocID);
                    Log.i(TAG, "New Voc Added: " + newVoc.toString());
                    ArrayList<Word> newWords = xmlParser.getWords();
                    for (Word word : newWords) {
                        this.addWord(word, newVoc);
                        Log.i(TAG, "New Word Added: " + word.toString());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addLanguagesFromEnum() {
        for (Language language : Language.values()){
            try {
                addLanguage(language);
            }catch (Exception e){
                Log.i(TAG, "-- Trying to add " + language.name() + ", but exist --");
            }
        }
    }
}
