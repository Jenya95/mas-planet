package com.sanevich.mas.service;

import com.sanevich.mas.model.*;
import com.sanevich.mas.model.item.Alien;
import com.sanevich.mas.model.item.Base;
import com.sanevich.mas.model.item.Resource;

import java.io.IOException;
import java.util.Arrays;

import static com.sanevich.mas.model.item.AlienState.*;

public class Steps {

    private static int stepCount = 0;

    public static void doStep(Planet planet) throws IOException {
        showMap(planet);
        for (int i = 0; i < planet.getField().length; i++) {
            for (int j = 0; j < planet.getField()[i].length; j++) {
                if (planet.getField()[i][j].getItem() instanceof Alien) {
                    if ((i != planet.getField().length - 1) && stepCount == 0 && (!(didMakeStep(planet.getField()[i][j])))) {
                        planet.getField()[i + 1][j].setItem(planet.getField()[i][j].getItem());
                        //в начале все агенты делают шаг и переходят в состояние SEARCHING
                        ((Alien) planet.getField()[i + 1][j].getItem()).getAlienStates().addAll(Arrays.asList
                                (SEARCHING, MAKE_A_STEP));
                        planet.getField()[i][j].setItem(null);
                    } else if ((i != planet.getField().length - 1) && !didMakeStep(planet.getField()[i][j])) {
                        planet.getField()[i + 1][j].setItem(planet.getField()[i][j].getItem());
                        //а если не начало то просто делают один шаг
                        ((Alien) planet.getField()[i + 1][j].getItem()).getAlienStates().add(MAKE_A_STEP);
                        planet.getField()[i][j].setItem(null);
                    }
                }
            }
        }

        for (int i = 0; i < planet.getField().length; i++) {
            for (int j = 0; j < planet.getField()[i].length; j++) {
                if (planet.getField()[i][j].getItem() instanceof Alien) {
                    //после определения всех шагов снимаем пометку про шаг
                    ((Alien) planet.getField()[i][j].getItem()).getAlienStates().remove(MAKE_A_STEP);
                }
            }
        }
        stepCount++;
    }

    private static boolean didMakeStep(Cell cell) {
        return ((Alien) cell.getItem())
                .getAlienStates().contains(MAKE_A_STEP);
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
