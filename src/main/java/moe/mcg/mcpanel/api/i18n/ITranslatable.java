package moe.mcg.mcpanel.api.i18n;

/**
 * 可翻译对象的接口。
 * <p>
 * 实现此接口的对象需要提供自己的翻译逻辑，具体通过 {@link #translate()} 方法进行翻译。
 * </p>
 */
public interface ITranslatable {

    /**
     * 翻译对象。
     * <p>
     * 实现此方法时，需提供将对象翻译为目标语言的逻辑。
     * </p>
     */
    void translate();
}
