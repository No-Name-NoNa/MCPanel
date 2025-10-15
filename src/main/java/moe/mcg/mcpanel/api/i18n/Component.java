package moe.mcg.mcpanel.api.i18n;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

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

    public static Component literal(String text) {
        return new Component(text, null, null, false);
    }

    public static Component translatable(String key, Object... args) {
        return new Component(null, key, args, true);
    }

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
