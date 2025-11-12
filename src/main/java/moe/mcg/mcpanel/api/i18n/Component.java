package moe.mcg.mcpanel.api.i18n;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

/**
 * 表示一个文本组件，可以是字面文本（不可翻译）或可翻译文本。
 * <p>
 * 该类用于管理和处理可以被翻译的文本，支持字面文本（不进行翻译）和需要翻译的文本。
 * 对于可翻译文本，支持参数化替换和动态内容插入。
 * </p>
 */
public class Component {
    private final String key;
    private final Object[] args;
    private final String literalText;
    private final boolean translatable;

    private Component(String literalText, String key, Object[] args, boolean translatable) {
        this.literalText = literalText;
        this.key = key;
        this.args = args;
        this.translatable = translatable;
    }

    /**
     * 创建一个字面文本组件（不可翻译）。
     * <p>
     * 该方法创建的文本组件不会经过翻译处理，直接使用传入的文本。
     * </p>
     *
     * @param text 要显示的字面文本
     * @return 一个字面文本的组件
     */
    public static Component literal(String text) {
        return new Component(text, null, null, false);
    }

    /**
     * 创建一个可翻译的文本组件。
     * <p>
     * 该方法创建的文本组件需要根据指定的翻译键获取翻译，支持通过参数替换文本中的占位符。
     * </p>
     *
     * @param key 翻译键
     * @param args 替换文本中的占位符的参数
     * @return 一个可翻译的文本组件
     */
    public static Component translatable(String key, Object... args) {
        return new Component(null, key, args, true);
    }

    /**
     * 获取该组件的字符串表示。
     * <p>
     * 如果是字面文本，则直接返回文本；如果是可翻译文本，则尝试获取翻译并用传入的参数替换占位符。
     * </p>
     *
     * @return 该组件的字符串表示
     */
    public String getString() {
        if (!translatable) {
            return literalText;
        }

        String raw = I18n.get(key);
        if (args != null && args.length > 0) {
            Object[] resolved = Arrays.stream(args)
                    .map(arg -> arg instanceof Component ? ((Component) arg).getString() : arg)
                    .toArray();
            raw = MessageFormat.format(raw, resolved);
        }
        return raw;
    }

    @Override
    public String toString() {
        return getString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Component c)) return false;
        return translatable == c.translatable &&
                Objects.equals(key, c.key) &&
                Objects.deepEquals(args, c.args) &&
                Objects.equals(literalText, c.literalText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, Arrays.hashCode(args), literalText, translatable);
    }
}
