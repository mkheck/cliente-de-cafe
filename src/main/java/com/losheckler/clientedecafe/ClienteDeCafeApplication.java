package com.losheckler.clientedecafe;

import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.time.Instant;

@SpringBootApplication
public class ClienteDeCafeApplication {
    @Bean
    WebClient webClient() {
        return WebClient.create("http://localhost:8080/cafes");
    }

    public static void main(String[] args) {
        SpringApplication.run(ClienteDeCafeApplication.class, args);
    }
}

@Component
class Demo {
    private WebClient client;

    Demo(WebClient client) {
        this.client = client;
    }

    @PostConstruct
    private void ejecutar() {
        client.get()
                .retrieve()
                .bodyToFlux(Cafe.class)
                .filter(cafe -> cafe.getNombre().equalsIgnoreCase("kaldi's coffee"))
                .flatMap(cafe -> client.get()
                    .uri("/{id}/pedidos", cafe.getId())
                    .retrieve()
                    .bodyToFlux(PedidoDeCafe.class))
                .subscribe(System.out::println);
    }
}

@Value
class PedidoDeCafe {
    private String idDeCafe;
    private Instant marcaDeTiempo;
}

@Value
class Cafe {
    private String id;
    private String nombre;
}