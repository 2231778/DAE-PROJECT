package pt.ipleiria.estg.dei.ei.daeproject.academics.Enums;

public enum RoleType {
    ADMIN("ADMIN"),
    COLABORADOR("COLABORADOR"),
    RESPONSAVEL("RESPONSAVEL");

    private final String value;

    RoleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RoleType fromString(String text) {
        for (RoleType r : RoleType.values()) {
            if (r.value.equalsIgnoreCase(text)) return r;
        }
        throw new IllegalArgumentException("Unknown role: " + text);
    }
}
