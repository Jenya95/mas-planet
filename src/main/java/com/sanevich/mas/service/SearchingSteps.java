package com.sanevich.mas.service;

import com.sanevich.mas.model.Cell;
import com.sanevich.mas.model.item.Alien;
import com.sanevich.mas.model.item.AlienState;
import com.sanevich.mas.model.item.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static com.sanevich.mas.model.item.AlienState.*;
import static com.sanevich.mas.model.item.AlienState.DOWN;
import static com.sanevich.mas.service.Steps.checkIfPath;
import static com.sanevich.mas.service.Steps.checkIfResource;

class SearchingSteps {

    private static final Logger log = LoggerFactory.getLogger(SearchingSteps.class);

    static void makeSimpleStep(int y, int x, Cell[][] field) {
        //шаг только если при этой итерации еще не был сделан шаг
        if (!Steps.didMakeStep(field[y][x])) {
            //если агент движется FORWARD или если это первый шаг
            if (((Alien) field[y][x].getItem()).getAlienStates().contains(FORWARD) || Steps.getStepCount() == 0) {
                //если агент находится левее правой границы и движется FORWARD, то он должен сделать шаг вправо
                if (x + 1 < field[y].length) {
                    moveRight(field[y], x);
                }
                //а если дошел до границы и движется FORWARD и DOWN - то должен шагнуть вниз, но только если карта не
                //закончилась вниз и меняет направление
                else if (y + 1 < field.length && ((Alien) field[y][x].getItem()).getAlienStates().contains(DOWN)) {
                    moveDown(y, x, field, FORWARD);
                }
                //а если дошел до границы и движется FORWARD и UP - то должен шагнуть вверх, но только если карта не
                //закончилась вверх и меняет направление
                else if (y - 1 >= 0 && ((Alien) field[y][x].getItem()).getAlienStates().contains(UP)) {
                    moveUp(y, x, field, FORWARD);
                }
                //а если он в правом нижнем углу карты, то он меняет направление и идет влево
                else if (x == field[y].length-1 && y == field.length - 1){
                    moveLeftFromRightCorner(field[y], x, DOWN);
                }
                //а если он в правом верхнем углу карты, то он идет влево
                else {
                    moveLeftFromRightCorner(field[y], x, UP);
                }
            } else {
                //если агент движется BACKWARD
                if (((Alien) field[y][x].getItem()).getAlienStates().contains(BACKWARD)) {
                    //если агент находится правее левой границы и движется BACKWARD, то он должен сделать шаг влево
                    if (x - 1 >= 0) {
                        moveLeft(field[y], x);
                    }
                    //а если дошел до границы и движется BACKWARD и UP - то должен шагнуть вверх, но только если
                    // карта не закончилась вверх
                    else if ((y - 1 >= 0) && ((Alien) field[y][x].getItem()).getAlienStates().contains(UP)) {
                        moveUp(y, x, field, BACKWARD);
                    }
                    //а если дошел до границы и движется BACKWARD и DOWN - то должен шагнуть вниз, но только если
                    // карта не закончилась вниз
                    else if ((y + 1 < field.length) && ((Alien) field[y][x].getItem()).getAlienStates().contains(DOWN)) {
                        moveDown(y, x, field, BACKWARD);
                    }
                    //а если он в левом верхнем углу карты, то он меняет направление и идет вправо
                    else if (x == 0 && y == 0) {
                        moveRightFromLeftCorner(field[y], x, UP);
                    }
                    //а если он в левом нижнем углу карты, то он меняет направление и идет вправо
                    else if (x == 0 && y == field.length - 1) {
                        moveRightFromLeftCorner(field[y], x, DOWN);
                    }
                }
            }
        }
    }

    private static void moveRightFromLeftCorner(Cell[] cells, int x, AlienState upOrDown) {
        Cell required = new Cell(cells[x + 1]);
        nullIfEmptyResource(required);
        boolean resourceFound = checkIfResource(cells[x], required);
        boolean pathFound = checkIfPath(cells[x], required);
        if (!resourceFound && !pathFound) {
            cells[x + 1].setItem(cells[x].getItem());
            ((Alien) cells[x + 1].getItem()).getAlienStates().remove(upOrDown);
            ((Alien) cells[x + 1].getItem()).getAlienStates().remove(DOWN);
            ((Alien) cells[x + 1].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP,
                    FORWARD, SEARCHING, upOrDown == DOWN ? UP : DOWN));
            cells[x].setItem(required.getItem());
        }
    }

    private static void moveLeftFromRightCorner(Cell[] cells, int x, AlienState upOrDown) {
        Cell required = new Cell(cells[x-1]);
        nullIfEmptyResource(required);
        boolean resourceFound = checkIfResource(cells[x], cells[x-1]);
        boolean pathFound = checkIfPath(cells[x], cells[x-1]);
        if (!resourceFound && !pathFound) {
            cells[x - 1].setItem(cells[x].getItem());
            Alien alien = (Alien) cells[x - 1].getItem();
            alien.getAlienStates().remove(FORWARD);
            alien.getAlienStates().remove(upOrDown);
            alien.getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, BACKWARD,
                    SEARCHING, upOrDown == UP ? DOWN : UP));
            cells[x].setItem(required.getItem());
            log.info("moveLeftFromRightCorner {}", alien.getName());
        }
    }


    private static void moveUp(int y, int x, Cell[][] field, AlienState forwardOrBackward) {
        Cell required = new Cell(field[y - 1][x]);
        nullIfEmptyResource(required);
        boolean resourceFound = checkIfResource(field[y][x], field[y - 1][x]);
        boolean pathFound = checkIfPath(field[y][x], field[y - 1][x]);
        if (!resourceFound && !pathFound) {
            field[y - 1][x].setItem(field[y][x].getItem());
            Alien alien = (Alien) field[y - 1][x].getItem();
            alien.getAlienStates().remove(forwardOrBackward);
            alien.getAlienStates().addAll(Arrays.asList(MAKE_A_STEP,
                    forwardOrBackward == FORWARD ? BACKWARD : FORWARD, SEARCHING, UP));
            field[y][x].setItem(required.getItem());
            log.info("moveUp {}", alien.getName());
        }
    }

    private static void moveDown(int y, int x, Cell[][] field, AlienState forwardOrBackward) {
        Cell required = new Cell(field[y + 1][x]);
        nullIfEmptyResource(required);
        boolean resourceFound = checkIfResource(field[y][x], field[y + 1][x]);
        boolean pathFound = checkIfPath(field[y][x], field[y + 1][x]);
        if (!resourceFound && !pathFound) {
            field[y + 1][x].setItem(field[y][x].getItem());
            Alien alien = (Alien) field[y + 1][x].getItem();
            alien.getAlienStates().remove(forwardOrBackward);
            alien.getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, forwardOrBackward ==
                            FORWARD ? BACKWARD : FORWARD,
                    SEARCHING, DOWN));
            field[y][x].setItem(required.getItem());
            log.info("moveDown {}", alien.getName());
        }
    }

    private static void moveLeft(Cell[] cells, int x) {
        Cell required = new Cell(cells[x - 1]);
        nullIfEmptyResource(required);
        boolean resourceFound = checkIfResource(cells[x], required);
        boolean pathFound = checkIfPath(cells[x], required);
        if (!resourceFound && !pathFound) {
            cells[x - 1].setItem(cells[x].getItem());
            Alien alien = (Alien) cells[x - 1].getItem();
            alien.getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, BACKWARD, SEARCHING));
            cells[x].setItem(required.getItem());
            log.info("moveLeft {}", alien.getName());
        }
    }

    private static void moveRight(Cell[] cells, int x) {
        Cell required = new Cell(cells[x + 1]);
        nullIfEmptyResource(required);
        boolean resourceFound = checkIfResource(cells[x], cells[x + 1]);
        boolean pathFound = checkIfPath(cells[x], cells[x + 1]);
        if (!resourceFound && !pathFound) {
            cells[x + 1].setItem(cells[x].getItem());
            Alien alien = (Alien) cells[x + 1].getItem();
            if (alien.getAlienStates().contains(UP)) {
                alien.getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, FORWARD, SEARCHING, UP));
            } else {
                alien.getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, FORWARD,
                        SEARCHING, DOWN));
            }
            cells[x].setItem(required.getItem());
            log.info("moveRight {}", alien.getName());
        }
    }

    private static void nullIfEmptyResource(Cell required) {
        if (required.getItem() instanceof Resource && ((Resource) required.getItem()).getSize()==0) {
            required.setItem(null);
        }
    }
}
