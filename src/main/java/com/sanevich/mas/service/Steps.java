package com.sanevich.mas.service;

import com.sanevich.mas.model.*;
import com.sanevich.mas.model.item.*;
import com.sanevich.mas.pathfinding.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.sanevich.mas.model.item.AlienState.*;

public class Steps {

    private static final Logger log = LoggerFactory.getLogger(Steps.class);

    private static int stepCount = 0;

    private static Planet planet;

    public static void doStep(Planet planetOfAlien) throws IOException {
        Steps.planet = planetOfAlien;
        for (int i = 0; i < planet.getField().length; i++) {
            for (int j = 0; j < planet.getField()[i].length; j++) {
                planet.getField()[i][j].setX(j);
                planet.getField()[i][j].setY(i);
                if (planet.getField()[i][j].getItem() instanceof Alien) {
                    //в начале все агенты переходят в состояние SEARCHING
                    if (stepCount == 0) {
                        ((Alien) planet.getField()[i][j].getItem()).getAlienStates().add(SEARCHING);
                    }
                    //если в данной ячейке агент в состоянии SEARCHING - он должен куда-нибудь сдвинуться для поиска
                    //ресурсов
                    if (((Alien) planet.getField()[i][j].getItem()).getAlienStates().contains(SEARCHING)) {
                        makeSimpleStep(i, j, planet.getField());
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

    private static void makeSimpleStep(int y, int x, Cell[][] field) {
        //шаг только если при этой итерации еще не был сделан шаг
        if (!didMakeStep(field[y][x])) {
            //если агент движется FORWARD или если это первый шаг
            if (((Alien) field[y][x].getItem()).getAlienStates().contains(FORWARD) || stepCount == 0) {
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
        checkIfResource(cells[x], required);
        cells[x + 1].setItem(cells[x].getItem());
        ((Alien) cells[x + 1].getItem()).getAlienStates().remove(upOrDown);
        ((Alien) cells[x + 1].getItem()).getAlienStates().remove(DOWN);
        ((Alien) cells[x + 1].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP,
                FORWARD, SEARCHING, upOrDown == DOWN ? UP : DOWN));
        cells[x].setItem(required.getItem());
    }


    private static void moveLeftFromRightCorner(Cell[] cells, int x, AlienState upOrDown) {
        Cell required = new Cell(cells[x-1]);
        checkIfResource(cells[x], cells[x-1]);
        cells[x - 1].setItem(cells[x].getItem());
        ((Alien) cells[x - 1].getItem()).getAlienStates().remove(FORWARD);
        ((Alien) cells[x - 1].getItem()).getAlienStates().remove(upOrDown);
        ((Alien) cells[x - 1].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, BACKWARD,
                SEARCHING, upOrDown == UP ? DOWN : UP));
        cells[x].setItem(required.getItem());
    }


    private static void moveUp(int y, int x, Cell[][] field, AlienState forwardOrBackward) {
        Cell required = new Cell(field[y - 1][x]);
        checkIfResource(field[y][x], field[y - 1][x]);
        field[y - 1][x].setItem(field[y][x].getItem());
        ((Alien) field[y - 1][x].getItem()).getAlienStates().remove(forwardOrBackward);
        ((Alien) field[y - 1][x].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP,
                forwardOrBackward == FORWARD ? BACKWARD : FORWARD, SEARCHING, UP));
        field[y][x].setItem(required.getItem());
    }

    private static void moveDown(int y, int x, Cell[][] field, AlienState forwardOrBackward) {
        Cell required = new Cell(field[y + 1][x]);
        checkIfResource(field[y][x], field[y + 1][x]);
        field[y + 1][x].setItem(field[y][x].getItem());
        ((Alien) field[y + 1][x].getItem()).getAlienStates().remove(forwardOrBackward);
        ((Alien) field[y + 1][x].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, forwardOrBackward ==
                        FORWARD ? BACKWARD : FORWARD,
                SEARCHING, DOWN));
        field[y][x].setItem(required.getItem());
    }

    private static void moveLeft(Cell[] cells, int x) {
        Cell required = new Cell(cells[x - 1]);
        checkIfResource(cells[x], required);
        cells[x - 1].setItem(cells[x].getItem());
        ((Alien) cells[x - 1].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, BACKWARD, SEARCHING));
        cells[x].setItem(required.getItem());
    }

    private static void moveRight(Cell[] cells, int x) {
        Cell required = new Cell(cells[x + 1]);
        checkIfResource(cells[x], cells[x + 1]);
        cells[x + 1].setItem(cells[x].getItem());
        if (((Alien) cells[x + 1].getItem()).getAlienStates().contains(UP)) {
            ((Alien) cells[x + 1].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, FORWARD, SEARCHING, UP));
        } else {
            ((Alien) cells[x + 1].getItem()).getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, FORWARD,
                    SEARCHING, DOWN));
        }
        cells[x].setItem(required.getItem());
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

    private static boolean checkIfResource(Cell current, Cell required) {
        if (required.getItem() instanceof Resource) {
            Alien alien = (Alien) current.getItem();
            Resource resource = (Resource) required.getItem();

            alien.getAlienStates().remove(SEARCHING);
            alien.getAlienStates().add(COLLECTING);
            alien.getAlienStates().add(MOVING_TO_BASE);

            if (resource.getSize() > 0) {
                if (resource.getSize() > alien.getSizeOfBag()) {
                    resource.setSize(resource.getSize() - alien.getSizeOfBag());
                    alien.setResourcesInBag(alien.getSizeOfBag());
                } else {
                    alien.setResourcesInBag(resource.getSize());
                    resource.setSize(0);
                }
                log.info("Alien {} collect {} from resource {} and move to base", alien.getName(),
                        alien.getResourcesInBag(), resource.getName());

                List<Point> routeToBase;

                routeToBase = TrackUtilities.findRoute(new Point(required.getX(), required.getY()), new Point(0,0),
                        planet.getField());

                return true;
            }
        }
    }

}
