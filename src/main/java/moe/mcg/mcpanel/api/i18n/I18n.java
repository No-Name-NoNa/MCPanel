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

import static moe.mcg.mcpanel.Main.LOGGER;


/**
 * 国际化管理类，负责加载和管理不同语言的翻译文件。
 * <p>
 * 此类支持加载多种语言的翻译，并允许根据当前语言获取对应的翻译文本。
 * 通过 {@link I18n#loadAll()} 方法加载所有语言的翻译文件，支持动态切换语言，
 * 并使用 {@link I18n#setLanguage(Language)} 方法来设置当前语言。
 * </p>
 * <p>
 * 支持通过 {@link I18n#get(String)} 方法获取当前语言下的翻译文本，
 * 以及通过 {@link I18n#get(Language, String)} 方法获取指定语言的翻译文本。
 * </p>
 */
public class I18n {
    private static final Gson GSON = new Gson();
    private static final String LANG_PATH = "/assets/mcpanel/lang/";

    @Getter
    private static final Map<Language, Map<String, String>> allTranslations = new HashMap<>();

    @Getter
    private static Language currentLanguage = Language.EN_US;

    private static Map<String, String> translations = new HashMap<>();

    /**
     * 加载所有语言的翻译文件。
     * <p>
     * 该方法会遍历所有支持的语言，并尝试从资源文件中加载对应语言的翻译文件，
     * 文件路径为 "/assets/mcpanel/lang/{languageCode}.json"。
     * 如果某个语言的文件加载失败，默认使用空的翻译映射。
     * </p>
     */
    public static void loadAll() {
        LOGGER.info("Loading all translations");
        for (Language lang : Language.values()) {
            String fileName = LANG_PATH + lang.getCode() + ".json";
            try (InputStreamReader reader = new InputStreamReader(
                    Objects.requireNonNull(I18n.class.getResourceAsStream(fileName)),
                    StandardCharsets.UTF_8)) {

                Type type = new TypeToken<Map<String, String>>() {
                }.getType();
                Map<String, String> langMap = GSON.fromJson(reader, type);
                allTranslations.put(lang, langMap);
            } catch (Exception e) {
                allTranslations.put(lang, new HashMap<>());
            }
        }
        setLanguage(Language.EN_US);
    }

    /**
     * 设置当前语言并更新翻译映射。
     * <p>
     * 此方法会根据指定的 {@link Language} 设置当前语言，并更新 {@link I18n#translations} 以使用该语言的翻译。
     * </p>
     *
     * @param language 要设置的语言
     */
    public static void setLanguage(Language language) {
        currentLanguage = language;
        translations = allTranslations.getOrDefault(language, new HashMap<>());
        TranslationManager.translateAll();
    }

    /**
     * 获取当前语言下的翻译文本。
     * <p>
     * 如果指定的键没有对应的翻译文本，返回键本身。
     * </p>
     *
     * @param key 翻译文本的键
     * @return 当前语言下的翻译文本
     */
    public static String get(String key) {
        return translations.getOrDefault(key, key);
    }

    /**
     * 获取指定语言下的翻译文本。
     * <p>
     * 如果指定语言的翻译文件中没有对应的键，返回键本身。
     * </p>
     *
     * @param language 要获取翻译的语言
     * @param key 翻译文本的键
     * @return 指定语言下的翻译文本
     */
    public static String get(Language language, String key) {
        return allTranslations.getOrDefault(language, new HashMap<>()).getOrDefault(key, key);
    }

}
