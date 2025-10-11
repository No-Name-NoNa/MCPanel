package moe.mcg.mcpanel.i18n;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class Component {
    private final Supplier<String> supplier;

    private Component(Supplier<String> supplier) {
        this.supplier = supplier;
    }

    public static Component literal(String text) {
        return new Component(() -> text);
    }

    public static Component translatable(String key, Object... args) {
        return new Component(() -> {
            String raw = I18n.get(key);
            if (args.length > 0)
                raw = MessageFormat.format(raw, args);
            return raw;
        });
    }

    public String getString() {
        return supplier.get();
    }

    @Override
    public String toString() {
        return getString();
    }
}
