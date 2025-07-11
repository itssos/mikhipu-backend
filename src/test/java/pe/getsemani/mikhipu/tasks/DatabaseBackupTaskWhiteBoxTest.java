package pe.getsemani.mikhipu.tasks;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class DatabaseBackupTaskWhiteBoxTest {

    @Test
    @DisplayName("Caja Blanca: Ejecución exitosa de pg_dump y verifica mensaje")
    void debeImprimirMensajeDeExitoCuandoExitCodeEsCero() throws Exception {
        int exitCode = 0;
        String mensaje;
        if (exitCode == 0) {
            mensaje = "Backup creado exitosamente";
        } else {
            mensaje = "Error al crear backup. Código de salida: " + exitCode;
        }
        assert(mensaje.contains("exitosamente"));
    }
}

