package pe.getsemani.mikhipu.tasks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseBackupTaskTDDTest {

    @Test
    @DisplayName("TDD: Extraer nombre de base de datos desde JDBC URL")
    void testExtraerNombreBaseDatos() {
        DatabaseBackupTask task = new DatabaseBackupTask();
        String url = "jdbc:postgresql://localhost:5432/mikhipu";
        String nombre = task.extractDbName(url);
        assertEquals("mikhipu", nombre, "Debe devolver el nombre correcto de la base de datos");
    }
}

