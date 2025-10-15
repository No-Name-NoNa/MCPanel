package moe.mcg.mcpanel.api.i18n;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class I18n {
    private static final Gson GSON = new Gson();
    private static final String LANG_PATH = "/assets/mcpanel/lang/";

    @Getter
    private static final Map<Language, Map<String, String>> allTranslations = new HashMap<>();

    @Getter
    private static Language currentLanguage = Language.EN_US;

    private static Map<String, String> translations = new HashMap<>();

    public static void loadAll() {
        for (Language lang : Language.values()) {
            String fileName = LANG_PATH + lang.getCode() + ".json";
            try (InputStreamReader reader = new InputStreamReader(
                    Objects.requireNonNull(I18n.class.getResourceAsStream(fileName)),
                    StandardCharsets.UTF_8)) {

                Type type = new TypeToken<Map<String, String>>() {
                }.getType();
                Map<String, String> langMap = GSON.fromJson(reader, type);
                allTranslations.put(lang, langMap);
                System.out.println("[I18n] Loaded language: " + lang.getCode());
            } catch (Exception e) {
                System.err.println("[I18n] Failed to load language file: " + fileName);
                allTranslations.put(lang, new HashMap<>());
            }
        }
        setLanguage(Language.EN_US);
    }

    public static void setLanguage(Language language) {
        currentLanguage = language;
        translations = allTranslations.getOrDefault(language, new HashMap<>());
    }

    public static String get(String key) {
        return translations.getOrDefault(key, key);
    }


    public static String get(Language language, String key) {
        return allTranslations.getOrDefault(language, new HashMap<>()).getOrDefault(key, key);
    }

}
