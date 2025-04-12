package pe.getsemani.mikhipu.person.enums;

public enum RelationshipType {
    PADRE("Padre", "El padre del estudiante"),
    MADRE("Madre", "La madre del estudiante"),
    ABUELO("Abuelo", "El abuelo del estudiante"),
    ABUELA("Abuela", "La abuela del estudiante"),
    OTRO("Otro", "Otro parentesco no especificado");

    private final String code;
    private final String description;

    RelationshipType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
