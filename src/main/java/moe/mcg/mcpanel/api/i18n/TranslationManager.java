package moe.mcg.mcpanel.api.i18n;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * 管理 {@link ITranslatable} 对象并处理其翻译的工具类。
 * <p>
 * {@link TranslationManager} 类允许注册和注销 {@link ITranslatable} 对象，
 * 并一次性翻译所有已注册的对象。
 * </p>
 */
public class TranslationManager {
    private static final Set<ITranslatable> TRANSLATABLE = new CopyOnWriteArraySet<>();

    /**
     * 注册一个 {@link ITranslatable} 对象以便稍后翻译。
     * <p>
     * 该对象将被添加到翻译队列中，等待将来调用翻译方法时进行翻译。
     * </p>
     *
     * @param translatable 要注册的 {@link ITranslatable} 对象
     */
    public static void register(ITranslatable translatable) {
        TRANSLATABLE.add(translatable);
    }

    /**
     * 注销一个 {@link ITranslatable} 对象，阻止其被翻译。
     * <p>
     * 该对象将从翻译队列中移除，之后不会再被翻译。
     * </p>
     *
     * @param translatable 要注销的 {@link ITranslatable} 对象
     */
    public static void unregister(ITranslatable translatable) {
        TRANSLATABLE.remove(translatable);
    }

    /**
     * 翻译所有已注册的 {@link ITranslatable} 对象。
     * <p>
     * 此方法将遍历所有已注册的可翻译对象，并调用它们的 {@link ITranslatable#translate()} 方法进行翻译。
     * </p>
     */
    public static void translateAll() {
        for (ITranslatable t : TRANSLATABLE) {
            t.translate();
        }
    }
}
