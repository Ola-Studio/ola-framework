package io.ola.crud.enums;

import java.util.function.Function;

/**
 * @author yiuman
 * @date 2021/12/1
 */
public enum SensitiveStrategy {

    /**
     * Username sensitive strategy.
     */
    USERNAME(s -> s.replaceAll("(\\S)\\S(\\S*)", "$1*$2")),
    /**
     * Id card sensitive type.
     */
    ID_CARD(s -> s.replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1****$2")),
    /**
     * Phone sensitive type.
     */
    PHONE(s -> s.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")),
    /**
     * Address sensitive type.
     */
    ADDRESS(s -> s.replaceAll("(\\S{3})\\S{2}(\\S*)\\S{2}", "$1****$2****")),

    UUID(s -> s.replaceAll("(\\S{6})(\\S*)", "$1****"));


    private final Function<String, String> desensitizer;

    SensitiveStrategy(Function<String, String> desensitizer) {
        this.desensitizer = desensitizer;
    }

    public Function<String, String> getDesensitizer() {
        return desensitizer;
    }
}
