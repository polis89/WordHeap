package ru.polis.wordsheap.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ru.polis.wordsheap.enums.Language;
import ru.polis.wordsheap.objects.Vocabulary;
import ru.polis.wordsheap.objects.Word;

public class XMLParser {
    private static final String TAG = "XMLParserLog";
    private static final String VOCABULARY_TAG = "vocabulary";
    private static final String WORD_TAG = "word";
    private static final String VOCABULARY_NAME_ATTR = "name";
    private static final String VOCABULARY_LANGUAGE_ATTR = "language";
    private static final String WORD_NAME_ATTR = "name";
    private static final String WORD_TRANSLATE_ATTR = "translate";

    private boolean isVocabularyXML = true;

    private Vocabulary vocabulary;
    private ArrayList<Word> words;

    public XMLParser(InputStream fileInputStream) {
        if(isVocabularyXML) {
            words = new ArrayList<>();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if(line.contains(VOCABULARY_TAG)){
                        parseVocabulary(line);
                    } else if (line.contains(WORD_TAG)) {
                        parseWord(line);
                    } else {
                        isVocabularyXML = false;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Vocabulary getVocabulary() {
        return vocabulary;
    }

    public ArrayList<Word> getWords() {
        return words;
    }

    private void parseVocabulary(String line) {
        if(line.startsWith("</")) return;
        String name = null;
        Language language = null;
        String[] splitLine = line.split("\"");
        for(int i = 0; i < splitLine.length; i++){
            if(splitLine[i].contains(VOCABULARY_NAME_ATTR)){
                i++; // Take next from array
                name = splitLine[i];
            } else if (splitLine[i].contains(VOCABULARY_LANGUAGE_ATTR)){
                i++;
                language = Language.valueOf(splitLine[i]);
            }
        }
        if(name == null || language == null){
            isVocabularyXML = false;
            return;
        }
        vocabulary = new Vocabulary(name, language);
    }

    private void parseWord(String line) {
        String name = null;
        String translate = null;
        String[] splitLine = line.split("\"");
        for(int i = 0; i < splitLine.length; i++){
            if(splitLine[i].contains(WORD_NAME_ATTR)){
                i++; // Take next from array
                name = splitLine[i];
            } else if (splitLine[i].contains(WORD_TRANSLATE_ATTR)){
                i++;
                translate = splitLine[i];
            }
        }
        if(name == null || translate == null){
            isVocabularyXML = false;
            return;
        }
        words.add(new Word(name, translate));
    }

    public boolean fileIsVocabularyXML() {
        return isVocabularyXML;
    }
}
