package pe.getsemani.mikhipu.tasks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseBackupTaskBlackBoxTest {

    @Test
    @DisplayName("Caja Negra: Debe extraer el nombre correcto de la base de datos desde el JDBC URL sin conocer la implementación interna")
    void shouldGenerateBackupFileWithValidName() throws Exception {

        DatabaseBackupTask task = new DatabaseBackupTask();
        String jdbcUrl = "jdbc:postgresql://localhost:5432/testdb";

        Field dbUrlField = DatabaseBackupTask.class.getDeclaredField("dbUrl");
        dbUrlField.setAccessible(true);
        dbUrlField.set(task, jdbcUrl);

        Method extractMethod = DatabaseBackupTask.class.getDeclaredMethod("extractDbName", String.class);
        extractMethod.setAccessible(true);
        String dbName = (String) extractMethod.invoke(task, jdbcUrl);

        assertEquals("testdb", dbName);
    }
}
