package pe.getsemani.mikhipu.role.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enumeración de permisos con su código y etiqueta legible.
 */
public enum PermissionConstants {
    GET_PERSONS("GET_PERSONS", "Ver personas"),
    GET_PERSON("GET_PERSON", "Detalle de persona"),
    CREATE_PERSON("CREATE_PERSON", "Crear persona"),
    UPDATE_PERSON("UPDATE_PERSON", "Editar persona"),
    DELETE_PERSON("DELETE_PERSON", "Eliminar persona"),

    GET_ROLES("GET_ROLES", "Ver roles"),
    GET_ROLE("GET_ROLE", "Detalle de rol"),
    CREATE_ROLE("CREATE_ROLE", "Crear rol"),
    UPDATE_ROLE("UPDATE_ROLE", "Editar rol"),
    DELETE_ROLE("DELETE_ROLE", "Eliminar rol"),

    ASSIGN_ROLE_PERMISSION("ASSIGN_ROLE_PERMISSION", "Asignar permiso"),
    REMOVE_ROLE_PERMISSION("REMOVE_ROLE_PERMISSION", "Quitar permiso");

    private final String code;
    private final String label;

    private static final Map<String, PermissionConstants> BY_CODE =
            Stream.of(values()).collect(Collectors.toMap(PermissionConstants::getCode, pc -> pc));

    PermissionConstants(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * @return el identificador interno del permiso (ej. "GET_PERSONS").
     */
    public String getCode() {
        return code;
    }

    /**
     * @return la etiqueta legible para mostrar en UI (ej. "Ver personas").
     */
    public String getLabel() {
        return label;
    }

    /**
     * Busca la constante por su código.
     *
     * @param code el código del permiso
     * @return la constante correspondiente
     * @throws IllegalArgumentException si no existe una constante con ese código
     */
    public static PermissionConstants fromCode(String code) {
        PermissionConstants pc = BY_CODE.get(code);
        if (pc == null) {
            throw new IllegalArgumentException("Permiso desconocido: " + code);
        }
        return pc;
    }
}