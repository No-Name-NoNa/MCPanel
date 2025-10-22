package moe.mcg.mcpanel.api.i18n;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class TranslateManager {
    private static final Set<ITranslatable> TRANSLATABLE = new CopyOnWriteArraySet<>();

    public static void register(ITranslatable translatable) {
        TRANSLATABLE.add(translatable);
    }

    public static void unregister(ITranslatable translatable) {
        TRANSLATABLE.remove(translatable);
    }

    public static void translateAll() {
        for (ITranslatable t : TRANSLATABLE) {
            t.translate();
        }
    }
}
