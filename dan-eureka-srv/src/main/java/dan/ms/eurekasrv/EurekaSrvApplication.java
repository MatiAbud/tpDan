package dan.ms.eurekasrv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaSrvApplication {

	public static void main(String[] args) {
		System.out.println("SPRING_EUREKA_PORT: " + System.getenv("SPRING_EUREKA_PORT"));
		SpringApplication.run(EurekaSrvApplication.class, args);
	}

}
