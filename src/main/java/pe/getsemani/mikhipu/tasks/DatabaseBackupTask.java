package pe.getsemani.mikhipu.tasks;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DatabaseBackupTask {

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    private static final String BACKUP_FOLDER = "D:/UTP/CICLO9/INTEGRADOR II/proyecto/mikhipu-backend/backups";


    //@Scheduled(cron = "0 */5 * * * *") // Cada 5 minutos
    //@Scheduled(cron = "0 0 13 * * SUN") // Todos los domingos a las 3:00 AM
    //@Scheduled(cron = "0 0 15 1 * *") // Día 1 de cada mes a las 2:00 AM
    //@Scheduled(cron = "0 0 16 * * *") // Todos los días a las 4:00 PM
    @Scheduled(cron = "0 * * * * *") // cada minuto
    public void backupDatabase() {
        String dbName = extractDbName(dbUrl);
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String backupFile = BACKUP_FOLDER + File.separator + dbName + "_" + timestamp + ".sql";

        ProcessBuilder pb = new ProcessBuilder(
                "pg_dump",
                "-U", dbUser,
                "-F", "c",
                "-f", backupFile,
                dbName
        );


        pb.environment().put("PGPASSWORD", dbPassword);
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Backup creado exitosamente: " + backupFile);
            } else {
                System.err.println("Error al crear backup. Código de salida: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String extractDbName(String jdbcUrl) {

        return jdbcUrl.substring(jdbcUrl.lastIndexOf("/") + 1);
    }
}
