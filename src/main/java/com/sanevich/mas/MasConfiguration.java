package com.sanevich.mas;

import com.sanevich.mas.model.Cell;
import com.sanevich.mas.model.Planet;
import com.sanevich.mas.model.item.Alien;
import com.sanevich.mas.model.item.Base;
import com.sanevich.mas.model.item.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;

import static com.sanevich.mas.core.CommonData.*;

@Configuration
public class MasConfiguration {

    @Bean
    public Planet planet() {
        Planet planet = Planet.builder()
                .field(new Cell[HEIGHT_MAP][WIDTH_MAP])
                .build();

        Base base = new Base(0);

        Alien a1 = Alien.builder()
                .sizeOfBag(3)
                .name("a1")
                .alienStates(new HashSet<>())
                .build();

        Alien a2 = Alien.builder()
                .sizeOfBag(2)
                .name("a2")
                .alienStates(new HashSet<>())
                .build();

        Alien a3 = Alien.builder()
                .sizeOfBag(2)
                .name("a3")
                .alienStates(new HashSet<>()
                ).build();

        Resource r1 = new Resource(10, "Gold");
        Resource r2 = new Resource(30, "Silver");
        Resource r3 = new Resource(15, "Crypto");

        planet.initializeFiled();

        planet.addItemOnField(base, X_BASE_COORDINATE, Y_BASE_COORDINATE);
        planet.addItemOnField(a1, 1,10);
        planet.addItemOnField(a2, 7,13);
        planet.addItemOnField(r2, 2,12);
        planet.addItemOnField(r1, 10,5);
        planet.addItemOnField(r3, 13,14);
        planet.addItemOnField(a3, 5,7);

        return planet;
    }

}
