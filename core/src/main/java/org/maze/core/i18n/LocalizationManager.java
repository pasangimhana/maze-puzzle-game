package org.maze.core.i18n;

import java.util.Locale;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.Normalizer;
import java.text.MessageFormat;

public class LocalizationManager {
    private static LocalizationManager instance;
    private ResourceBundle messages;
    private Locale currentLocale;
    private LocalDate gameDate;

    private LocalizationManager() {
        setLocale(Locale.getDefault());
        gameDate = LocalDate.now();
    }

    public static LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
        }
        return instance;
    }

    public void setLocale(Locale locale) {
        currentLocale = locale;
        messages = ResourceBundle.getBundle("messages", currentLocale);
    }

    public String getString(String key) {
        return messages.getString(key);
    }

    public String getFormattedString(String key, Object... args) {
        String pattern = getString(key);
        return MessageFormat.format(pattern, args);
    }

    public String getLocalizedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", currentLocale);
        return gameDate.format(formatter);
    }

    public void advanceGameDate() {
        gameDate = gameDate.plusDays(1);
    }

    public String normalizeItemName(String name) {
        return Normalizer.normalize(name, Normalizer.Form.NFC);
    }
}
