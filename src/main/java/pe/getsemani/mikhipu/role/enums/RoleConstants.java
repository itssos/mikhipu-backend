package pe.getsemani.mikhipu.role.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enumeración de roles con su código interno y etiqueta legible.
 */
public enum RoleConstants {
    ADMINISTRADOR("ADMINISTRADOR", "Administrador"),
    ESTUDIANTE   ("ESTUDIANTE",    "Estudiante"),
    DOCENTE      ("DOCENTE",       "Docente"),
    APODERADO    ("APODERADO",     "Apoderado");

    private final String code;
    private final String label;

    // Mapa para lookup rápido por código
    private static final Map<String, RoleConstants> BY_CODE =
            Stream.of(values()).collect(Collectors.toMap(RoleConstants::getCode, rc -> rc));

    RoleConstants(String code, String label) {
        this.code  = code;
        this.label = label;
    }

    /** @return el identificador interno del rol (ej. "ADMINISTRADOR") */
    public String getCode() {
        return code;
    }

    /** @return la etiqueta legible para UI (ej. "Administrador") */
    public String getLabel() {
        return label;
    }

    /**
     * Busca la constante por su código.
     * @param code el código del rol
     * @return la constante correspondiente
     * @throws IllegalArgumentException si no existe una constante con ese código
     */
    public static RoleConstants fromCode(String code) {
        RoleConstants rc = BY_CODE.get(code);
        if (rc == null) {
            throw new IllegalArgumentException("Rol desconocido: " + code);
        }
        return rc;
    }
}
