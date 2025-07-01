package pe.getsemani.mikhipu.chat.controller;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Test de ChatFileController")
class ChatFileControllerTest {

    ChatFileController controller;

    @BeforeEach
    void setUp() {
        controller = new ChatFileController();
    }

    // --------- Unitario ---------
    @Test
    @DisplayName("Unitario: Sube archivo y retorna ruta correctamente")
    void testUploadFileUnitario() throws Exception {
        // Simula archivo
        byte[] content = "hola".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "prueba.txt", "text/plain", content);

        // Ejecuta
        String result = controller.uploadFile(file);

        // Verifica
        assertTrue(result.startsWith("/uploads/"));
        assertTrue(result.endsWith("_prueba.txt"));
        // El archivo realmente se guarda en el sistema de archivos
        String rutaReal = "uploads/" + result.substring("/uploads/".length());
        assertTrue(Files.exists(Paths.get(rutaReal)));
        // Limpieza
        Files.deleteIfExists(Paths.get(rutaReal));
    }

    // --------- Caja Negra ---------
    @Nested
    @DisplayName("Caja negra: API uploadFile")
    class BlackBoxApi {

        MockMvc mockMvc;

        @BeforeEach
        void setUpMvc() {
            mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        }

        @Test
        @DisplayName("Retorna /uploads/ al subir cualquier archivo")
        void testUploadApi() throws Exception {
            MockMultipartFile file = new MockMultipartFile("file", "f.txt", "text/plain", "abc".getBytes());
            mockMvc.perform(multipart("/api/chat/upload").file(file))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.startsWith("/uploads/")));
            // Limpieza de archivos
            // (Busca el archivo real y lo borra)
            Path dir = Paths.get("uploads");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*_f.txt")) {
                for (Path entry : stream) Files.deleteIfExists(entry);
            } catch (NoSuchFileException ignored) {}
        }
    }

    // --------- Caja Blanca (Errores internos) ---------
    @Nested
    @DisplayName("Caja blanca: Errores internos")
    class WhiteBoxInternal {

        @Test
        @DisplayName("Lanza RuntimeException si hay error de IO")
        void testIOException() throws Exception {
            MultipartFile fileMock = mock(MultipartFile.class);
            when(fileMock.getOriginalFilename()).thenReturn("fail.txt");
            when(fileMock.getInputStream()).thenThrow(new IOException("no se puede leer"));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.uploadFile(fileMock));
            assertTrue(ex.getMessage().contains("Error al guardar archivo"));
        }
    }

    // --------- TDD ---------
    @Test
    @DisplayName("TDD: El nombre devuelto debe tener un UUID único seguido del nombre del archivo original")
    void tddUploadNombreIncluyeUuidYNombreOriginal() {
        MockMultipartFile file = new MockMultipartFile("file", "original.doc", "application/msword", "x".getBytes());
        String result = controller.uploadFile(file);
        // Simula TDD: la funcionalidad ya existe, el test la describe como requerimiento
        String[] parts = result.replace("/uploads/", "").split("_", 2);
        assertEquals(2, parts.length, "Debe tener UUID y nombre original separados por _");
        assertDoesNotThrow(() -> UUID.fromString(parts[0]));
        assertEquals("original.doc", parts[1]);
        // Limpieza
        String rutaReal = "uploads/" + result.substring("/uploads/".length());
        assertTrue(Files.exists(Paths.get(rutaReal)));
        // Limpia archivo
        assertDoesNotThrow(() -> Files.deleteIfExists(Paths.get(rutaReal)));
    }

}
