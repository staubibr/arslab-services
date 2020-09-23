package application;
 
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
 
@SpringBootApplication
@ComponentScan({"controllers"})
public class ServicesApplication  extends SpringBootServletInitializer {
 
    public static void main(String[] args) {
        SpringApplication.run(ServicesApplication.class, args);
    }
}