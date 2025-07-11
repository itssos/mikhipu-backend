package pe.getsemani.mikhipu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MikhipuApplication {

	public static void main(String[] args) {
		SpringApplication.run(MikhipuApplication.class, args);
	}

}
