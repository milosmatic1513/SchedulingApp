package com.example.scheduleme.DataClasses;

// Holds the name and locale string of a language
public class Language {
    private String language;
    private String languageLocale;

    public Language(String language, String languageLocale) {
        this.language = language;
        this.languageLocale = languageLocale;
    }

    public String getLanguage() {
        return language;
    }

    public String getLanguageLocale() {
        return languageLocale;
    }

    @Override
    public String toString() {
        return language;
    }
}