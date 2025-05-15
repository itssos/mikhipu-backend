package pe.getsemani.mikhipu.role.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enumeración de permisos con su código y etiqueta legible.
 */
public enum PermissionConstants {
    ASSIGN_REPRESENTATIVES("ASSIGN_REPRESENTATIVES", "Asignar representantes"),
    ASSIGN_ROLE_PERMISSION("ASSIGN_ROLE_PERMISSION", "Asignar permisos a rol"),
    ASSIGN_SCHEDULE("ASSIGN_SCHEDULE", "Asignar horario"),
    ASSIGN_STUDENTS("ASSIGN_STUDENTS", "Asignar estudiantes"),
    ASSIGN_TEACHERS("ASSIGN_TEACHERS", "Asignar docentes"),
    CREATE_COURSE("CREATE_COURSE", "Crear curso"),
    CREATE_ENROLLMENT("CREATE_ENROLLMENT", "Crear inscripción"),
    CREATE_PERSON("CREATE_PERSON", "Crear persona"),
    CREATE_REPRESENTATIVE("CREATE_REPRESENTATIVE", "Crear representante"),
    CREATE_ROLE("CREATE_ROLE", "Crear rol"),
    CREATE_STUDENT("CREATE_STUDENT", "Crear estudiante"),
    CREATE_TEACHER("CREATE_TEACHER", "Crear docente"),
    DELETE_COURSE("DELETE_COURSE", "Eliminar curso"),
    DELETE_PERSON("DELETE_PERSON", "Eliminar persona"),
    DELETE_REPRESENTATIVE("DELETE_REPRESENTATIVE", "Eliminar representante"),
    DELETE_ROLE("DELETE_ROLE", "Eliminar rol"),
    DELETE_STUDENT("DELETE_STUDENT", "Eliminar estudiante"),
    DELETE_TEACHER("DELETE_TEACHER", "Eliminar docente"),
    GET_COURSE("GET_COURSE", "Detalle de curso"),
    GET_COURSES("GET_COURSES", "Ver cursos"),
    GET_COURSE_STUDENTS("GET_COURSE_STUDENTS", "Ver estudiantes del curso"),
    GET_ENROLLMENTS("GET_ENROLLMENTS", "Ver inscripciones"),
    GET_PERMISSION("GET_PERMISSION", "Detalle de permiso"),
    GET_PERMISSIONS("GET_PERMISSIONS", "Ver permisos"),
    GET_PERSON("GET_PERSON", "Detalle de persona"),
    GET_PERSONS("GET_PERSONS", "Ver personas"),
    GET_REPRESENTATIVE("GET_REPRESENTATIVE", "Detalle de representante"),
    GET_REPRESENTATIVES("GET_REPRESENTATIVES", "Ver representantes"),
    GET_ROLE("GET_ROLE", "Detalle de rol"),
    GET_ROLES("GET_ROLES", "Ver roles"),
    GET_STUDENT("GET_STUDENT", "Detalle de estudiante"),
    GET_STUDENTS("GET_STUDENTS", "Ver estudiantes"),
    GET_STUDENT_REPRESENTATIVES("GET_STUDENT_REPRESENTATIVES", "Ver representantes del estudiante"),
    GET_TEACHER("GET_TEACHER", "Detalle de docente"),
    GET_TEACHERS("GET_TEACHERS", "Ver docentes"),
    GET_TEACHERS_OF_COURSE("GET_TEACHERS_OF_COURSE", "Ver docentes del curso"),
    GET_TEACHER_STUDENTS("GET_TEACHER_STUDENTS", "Ver estudiantes del docente"),
    REMOVE_REPRESENTATIVES("REMOVE_REPRESENTATIVES", "Remover representantes"),
    REMOVE_ROLE_PERMISSION("REMOVE_ROLE_PERMISSION", "Remover permiso de rol"),
    REMOVE_SCHEDULE("REMOVE_SCHEDULE", "Remover horario"),
    REMOVE_STUDENTS("REMOVE_STUDENTS", "Remover estudiantes"),
    REMOVE_TEACHERS("REMOVE_TEACHERS", "Remover docentes"),
    UPDATE_COURSE("UPDATE_COURSE", "Actualizar curso"),
    UPDATE_ENROLLMENT("UPDATE_ENROLLMENT", "Actualizar inscripción"),
    UPDATE_ROLE("UPDATE_ROLE", "Actualizar rol"),
    UPDATE_STUDENT("UPDATE_STUDENT", "Actualizar estudiante"),
    UPDATE_TEACHER("UPDATE_TEACHER", "Actualizar docente"),
    UPLOAD_STUDENT_EXCEL("UPLOAD_STUDENT_EXCEL", "Subir estudiantes por Excel"),
    UPLOAD_STUDENT_LIST("UPLOAD_STUDENT_LIST", "Subir lista de estudiantes"),
    VIEW_SCHEDULES("VIEW_SCHEDULES", "Ver horarios");


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