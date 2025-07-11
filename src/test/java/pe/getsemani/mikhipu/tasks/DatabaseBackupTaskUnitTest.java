package pe.getsemani.mikhipu.tasks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseBackupTaskUnitTest {

    @Test
    @DisplayName("Unitario: extractDbName devuelve solo el nombre de la base de datos")
    void extractDbName_debeDevolverNombreBase() {
        DatabaseBackupTask task = new DatabaseBackupTask();

        String url1 = "jdbc:postgresql://localhost:5432/mikhipu";
        assertEquals("mikhipu", task.extractDbName(url1));

        String url2 = "jdbc:postgresql://localhost:5432/otra_db";
        assertEquals("otra_db", task.extractDbName(url2));

        String url3 = "jdbc:postgresql://remotehost:5432/ejemplo123";
        assertEquals("ejemplo123", task.extractDbName(url3));
    }
}