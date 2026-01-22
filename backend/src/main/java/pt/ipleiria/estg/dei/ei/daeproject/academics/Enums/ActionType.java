package pt.ipleiria.estg.dei.ei.daeproject.academics.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ActionType {
    CREATE,
    UPDATE,
    DELETE;

    @JsonCreator
    public static ActionType fromString(String value) {
        for (ActionType a : ActionType.values()) {
            if (a.name().equalsIgnoreCase(value)) {
                return a;
            }
        }
        throw new IllegalArgumentException(
                "Invalid action type: '" + value +
                        "'. Allowed values: CREATE, UPDATE, DELETE."
        );
    }
}
