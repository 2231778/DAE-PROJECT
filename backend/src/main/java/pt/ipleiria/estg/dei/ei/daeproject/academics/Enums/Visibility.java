package pt.ipleiria.estg.dei.ei.daeproject.academics.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Visibility {
    VISIBLE,
    INVISIBLE;

    @JsonCreator
    public static Visibility fromString(String value) {
        for (Visibility v : Visibility.values()) {
            if (v.name().equalsIgnoreCase(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException(
                "Invalid visibility value: '" + value +
                        "'. Allowed values: VISIBLE, INVISIBLE"
        );
    }
}