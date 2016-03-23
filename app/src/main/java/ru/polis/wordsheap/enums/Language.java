package ru.polis.wordsheap.enums;

import ru.polis.wordsheap.R;

public enum Language {
    DE_RUS (R.string.languageDescriptionDeRus),
    DE_ENG (R.string.languageDescriptionDeEng),
    ENG_RUS (R.string.languageDescriptionEngRus),
    ENG_DE (R.string.languageDescriptionEngDe),
    RUS_ENG (R.string.languageDescriptionRusEng),
    RUS_DE (R.string.languageDescriptionRusDe);

    private final int descriptionID;

    Language(int descriptionID) {
        this.descriptionID = descriptionID;
    }

    public int getDescriptionID(){
        return descriptionID;
    }
}
