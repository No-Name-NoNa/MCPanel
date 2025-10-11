package moe.mcg.mcpanel.i18n;

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
    private static Map<String, String> translations = new HashMap<>();

    @Getter
    private static Language currentLanguage = Language.EN_US;

    public static void load(Language language) {
        currentLanguage = language;
        String fileName = LANG_PATH + language.getCode() + ".json";
        try (InputStreamReader reader = new InputStreamReader(
                Objects.requireNonNull(I18n.class.getResourceAsStream(fileName)),
                StandardCharsets.UTF_8)) {

            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            translations = GSON.fromJson(reader, type);
            System.out.println("Loaded language: " + language.getCode());
        } catch (Exception e) {
            System.err.println("Failed to load language file: " + fileName);
            translations = new HashMap<>();
        }
    }

    public static String get(String key) {
        return translations.getOrDefault(key, key);
    }
}
