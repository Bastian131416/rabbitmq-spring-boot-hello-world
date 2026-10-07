package com.example.rabbitmq_tutorials;

import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RabbitmqTutorialsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqTutorialsApplication.class, args);
    }

    @Bean
    CommandLineRunner menu(Sender sender) {
        return args -> {
            Scanner scanner = new Scanner(System.in);
            boolean running = true;

            while (running) {
                System.out.println("\n1. Enviar mensaje");
                System.out.println("2. Salir");
                System.out.print("Seleccione: ");
                String option = scanner.nextLine().trim();

                switch (option) {
                    case "1" -> {
                        System.out.print("Mensaje: ");
                        sender.sendMessage(scanner.nextLine());
                    }
                    case "2" -> running = false;
                    default -> System.out.println("Opción no válida");
                }
            }
        };
    }
}