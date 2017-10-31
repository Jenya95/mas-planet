package com.sanevich.mas;

import com.sanevich.mas.core.CommonData;
import com.sanevich.mas.model.Cell;
import com.sanevich.mas.model.Planet;
import com.sanevich.mas.model.item.Alien;
import com.sanevich.mas.model.item.Base;
import com.sanevich.mas.model.item.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
public class MasConfiguration {

    @Bean
    public Planet planet() {
        Planet planet = Planet.builder()
                .field(new Cell[CommonData.HEIGHT_MAP][CommonData.WIDTH_MAP])
                .build();

        Base base = new Base();

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

        Arrays.stream(planet.getField())
                .forEach(x -> {
                    for (int i = 0; i < CommonData.WIDTH_MAP; i++) {
                        x[i] = new Cell();
                    }
                });

        planet.getField()[0][0].setItem(base);
        planet.getField()[1][10].setItem(a1);
        planet.getField()[7][13].setItem(a2);
        planet.getField()[2][12].setItem(r2);
        planet.getField()[10][5].setItem(r1);
        planet.getField()[13][14].setItem(r3);
        planet.getField()[5][7].setItem(a3);

        return planet;
    }

}
