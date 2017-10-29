package com.sanevich.mas.service;

import com.sanevich.mas.model.*;
import com.sanevich.mas.model.item.Alien;
import com.sanevich.mas.model.item.Base;
import com.sanevich.mas.model.item.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

import static com.sanevich.mas.model.item.AlienState.*;

public class Steps {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static int stepCount = 0;

    public static void doStep(Planet planet) throws IOException {
        //showMap(planet);
        for (int i = 0; i < planet.getField().length; i++) {
            for (int j = 0; j < planet.getField()[i].length; j++) {
                if (planet.getField()[i][j].getItem() instanceof Alien) {
                    //если в данной ячейке агент - он должен куда-нибудь сдвинуться
                    makeStep(i,j,planet.getField());
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

    private static void makeStep(int y, int x, Cell[][] field) {
        //шаг только если при этой итерации еще не был сделан шаг
        if (!didMakeStep(field[y][x])) {
            //если агент движется FORWARD или если это первый шаг
            if (((Alien) field[y][x].getItem()).getAlienStates().contains(FORWARD) || stepCount == 0) {
                //если агент находится левее правой границы и движется FORWARD, то он должен сделать шаг вправо
                if (x + 1 < field[y].length) {
                    field[y][x + 1].setItem(field[y][x].getItem());
                    if (((Alien) field[y][x + 1].getItem()).getAlienStates().contains(UP)) {
                        ((Alien) field[y][x + 1].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, FORWARD, SEARCHING, UP));
                    } else {
                        ((Alien) field[y][x + 1].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, FORWARD,
                                SEARCHING, DOWN));
                    }
                    field[y][x].setItem(null);
                }
                //а если дошел до границы и движется FORWARD и DOWN - то должен шагнуть вниз, но только если карта не
                //закончилась вниз и меняет направление
                else if (y + 1 < field.length && ((Alien) field[y][x].getItem()).getAlienStates().contains(DOWN)) {
                    field[y + 1][x].setItem(field[y][x].getItem());
                    ((Alien) field[y + 1][x].getItem()).getAlienStates().remove(FORWARD);
                    ((Alien) field[y + 1][x].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, BACKWARD,
                            SEARCHING, DOWN));
                    field[y][x].setItem(null);
                }
                //а если дошел до границы и движется FORWARD и UP - то должен шагнуть вверх, но только если карта не
                //закончилась вверх и меняет направление
                else if (y - 1 >= 0 && ((Alien) field[y][x].getItem()).getAlienStates().contains(UP)) {
                    field[y - 1][x].setItem(field[y][x].getItem());
                    ((Alien) field[y - 1][x].getItem()).getAlienStates().remove(FORWARD);
                    ((Alien) field[y - 1][x].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, BACKWARD,
                            SEARCHING, UP));
                    field[y][x].setItem(null);
                }
                //а если он в правом нижнем углу карты, то он меняет направление и идет влево
                else if (x == field[y].length-1 && y == field.length - 1){
                    field[y][x - 1].setItem(field[y][x].getItem());
                    ((Alien) field[y][x - 1].getItem()).getAlienStates().remove(FORWARD);
                    ((Alien) field[y][x - 1].getItem()).getAlienStates().remove(DOWN);
                    ((Alien) field[y][x - 1].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, BACKWARD,
                            SEARCHING, UP));
                    field[y][x].setItem(null);
                }
                //а если он в правом верхнем углу карты, то он идет влево
                else {
                    field[y][x - 1].setItem(field[y][x].getItem());
                    ((Alien) field[y][x - 1].getItem()).getAlienStates().remove(FORWARD);
                    ((Alien) field[y][x - 1].getItem()).getAlienStates().remove(UP);
                    ((Alien) field[y][x - 1].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, BACKWARD,
                            SEARCHING, DOWN));
                    field[y][x].setItem(null);
                }
            } else {
                //если агент движется BACKWARD
                if (((Alien) field[y][x].getItem()).getAlienStates().contains(BACKWARD)) {
                    //если агент находится правее левой границы и движется BACKWARD, то он должен сделать шаг влево
                    if (x - 1 >= 0) {
                        field[y][x - 1].setItem(field[y][x].getItem());
                        ((Alien) field[y][x - 1].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, BACKWARD, SEARCHING));
                        field[y][x].setItem(null);
                    }
                    //а если дошел до границы и движется BACKWARD и UP - то должен шагнуть вверх, но только если
                    // карта не закончилась вверх
                    else if ((y - 1 >= 0) && ((Alien) field[y][x].getItem()).getAlienStates().contains(UP)) {
                        field[y - 1][x].setItem(field[y][x].getItem());
                        ((Alien) field[y - 1][x].getItem()).getAlienStates().remove(BACKWARD);
                        ((Alien) field[y - 1][x].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP,
                                FORWARD, SEARCHING, UP));
                        field[y][x].setItem(null);
                    }
                    //а если дошел до границы и движется BACKWARD и DOWN - то должен шагнуть вниз, но только если
                    // карта не закончилась вниз
                    else if ((y + 1 < field.length) && ((Alien) field[y][x].getItem()).getAlienStates().contains(DOWN)) {
                        field[y + 1][x].setItem(field[y][x].getItem());
                        ((Alien) field[y + 1][x].getItem()).getAlienStates().remove(BACKWARD);
                        ((Alien) field[y + 1][x].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP,
                                FORWARD, SEARCHING, DOWN));
                        field[y][x].setItem(null);
                    }
                    //а если он в левом верхнем углу карты, то он меняет направление и идет вправо
                    else if (x == 0 && y == 0) {
                        field[y][x + 1].setItem(field[y][x].getItem());
                        ((Alien) field[y][x + 1].getItem()).getAlienStates().remove(BACKWARD);
                        ((Alien) field[y][x + 1].getItem()).getAlienStates().remove(UP);
                        ((Alien) field[y][x + 1].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP,
                                FORWARD, SEARCHING, DOWN));
                        field[y][x].setItem(null);
                    }

                    //а если он в левом нижнем углу карты, то он меняет направление и идет вверх
                    else if (x == 0 && y == field.length - 1) {
                        field[y][x + 1].setItem(field[y][x].getItem());
                        ((Alien) field[y][x + 1].getItem()).getAlienStates().remove(BACKWARD);
                        ((Alien) field[y][x + 1].getItem()).getAlienStates().remove(DOWN);
                        ((Alien) field[y][x + 1].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP,
                                FORWARD, SEARCHING, UP));
                        field[y][x].setItem(null);
                    }
                }
            }
        }
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
