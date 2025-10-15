package moe.mcg.mcpanel.api.i18n;

import lombok.Getter;

public enum Language {
    EN_US("en_us"),
    ZH_CN("zh_cn");

    @Getter
    private final String code;

    Language(String code) {
        this.code = code;
    }

    public static Language fromCode(String code) {
        for (Language lang : values()) {
            if (lang.code.equalsIgnoreCase(code)) {
                return lang;
            }
        }
        return EN_US;
    }
}