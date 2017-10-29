package com.sanevich.mas.service;

import com.sanevich.mas.model.*;
import com.sanevich.mas.model.item.Alien;
import com.sanevich.mas.model.item.Base;
import com.sanevich.mas.model.item.Resource;

import java.io.IOException;

public class Steps {

    private static int stepCount = 0;

    public static void doStep(Planet planet) throws IOException {
        showMap(planet);
        for (int i = 0; i < planet.getField().length; i++) {
            for (int j = 0; j < planet.getField()[i].length; j++) {
                if (planet.getField()[i][j].getItem() instanceof Alien) {
                    if (i != 19) {
                        planet.getField()[i + 1][j].setItem(planet.getField()[i][j].getItem());
                        planet.getField()[i][j].setItem(null);
                    }
                }
            }
        }

        stepCount++;
    }

    private static void showMap(Planet planet) throws IOException {
        for (int i = 0; i < planet.getField().length; i++) {
            for (int j = 0; j < planet.getField()[i].length; j++) {
                if (planet.getField()[i][j].getItem() instanceof Alien) {
                    System.out.print(" +a+ ");
                } else if (planet.getField()[i][j].getItem() instanceof Base) {
                    System.out.print(" +b+ ");
                } else if (planet.getField()[i][j].getItem() instanceof Resource) {
                    System.out.print(" +r+ ");
                } else {
                    System.out.print("  -  ");
                }
            }
            System.out.println();
        }
        System.out.println("endline");
    }
}
