package com.sanevich.mas.service;

import com.sanevich.mas.model.*;
import com.sanevich.mas.model.item.*;
import com.sanevich.mas.pathfinding.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sanevich.mas.model.item.AlienState.*;

public class Steps {

    private static final Logger log = LoggerFactory.getLogger(Steps.class);

    private static int stepCount = 0;

    private static Planet planet;
    private static Map<Point,List<Point>> routesToBase = new HashMap<>();

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
                    } else
                    //если в данной ячейке агент в состоянии MOVING_TO_BASE - он должен двигаться в сторону базы со
                    //своим ресурсом
                    if (((Alien) planet.getField()[i][j].getItem()).getAlienStates().contains(MOVING_TO_BASE)) {
                        makeStepToBase(i, j, planet.getField());
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
    private static void makeStepToBase(int y, int x, Cell[][] field) {
        //шаг только если при этой итерации еще не был сделан шаг
        if (!didMakeStep(field[y][x])) {
            Alien alien = (Alien) field[y][x].getItem();
            //если это первый шаг после того, как агент взял ресурс, то точка на которой он стоит еще не находится в
            //списке точек пути к базе
            if (!alien.getRouteToBase().contains(new Point(x,y))) {
                Cell cell = new Cell(field[alien.getRouteToBase().get(0).getyPosition()][alien.getRouteToBase().get(0).getxPosition()]);
                alien.getAlienStates().add(MAKE_A_STEP);
                field[alien.getRouteToBase().get(0).getyPosition()][alien.getRouteToBase().get(0).getxPosition()]
                        .setItem(alien);
                field[y][x].setItem(cell.getItem());
            }
            //иначе это не первый шаг после того, как агент взял ресурс, значит точка на которой он стоит находится в
            //списке точек пути к базе, а следующая точка - следующая в этом списке
            else {
                int newPointIndex = alien.getRouteToBase().indexOf(new Point(x,y)) + 1;
                if (newPointIndex < alien.getRouteToBase().size() - 1) { //-1 чтобы не тронуть базу
                    Cell nextCell = field[alien.getRouteToBase().get(newPointIndex).getyPosition()][alien.getRouteToBase().get(newPointIndex).getxPosition()];
                    Cell cell = new Cell(nextCell);
                    alien.getAlienStates().add(MAKE_A_STEP);
                    nextCell.setItem(alien);
                    field[y][x].setItem(cell.getItem());
                }
                //агент дошел до базы
                else {
                    log.info("Alien {} dropped {} resources at base", alien.getName(), alien.getResourcesInBag());
                    alien.setResourcesInBag(0);
                    alien.getAlienStates().remove(MOVING_TO_BASE);
                    alien.getAlienStates().add(MOVING_TO_RESORCE);
                    alien.getAlienStates().add(MAKE_A_STEP);
                }
            }
        }
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
        boolean resourceFound = checkIfResource(cells[x], required);
        if (!resourceFound) {
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
        boolean resourceFound = checkIfResource(cells[x], cells[x-1]);
        if (!resourceFound) {
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
        boolean resourceFound = checkIfResource(field[y][x], field[y - 1][x]);
        if (!resourceFound) {
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
        boolean resourceFound = checkIfResource(field[y][x], field[y + 1][x]);
        if (!resourceFound) {
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
        boolean resourceFound = checkIfResource(cells[x], required);
        if (!resourceFound) {
            cells[x - 1].setItem(cells[x].getItem());
            Alien alien = (Alien) cells[x - 1].getItem();
            alien.getAlienStates().addAll(Arrays.asList(MAKE_A_STEP, BACKWARD, SEARCHING));
            cells[x].setItem(required.getItem());
            log.info("moveLeft {}", alien.getName());
        }
    }

    private static void moveRight(Cell[] cells, int x) {
        Cell required = new Cell(cells[x + 1]);
        boolean resourceFound = checkIfResource(cells[x], cells[x + 1]);
        if (!resourceFound) {
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

            //если агент шел к этому ресурсу, а он теперь пустой - то агент переходит в состояние поиска нового ресурса
            if (((Resource) required.getItem()).getSize() == 0) {
                if (alien.getAlienStates().contains(MOVING_TO_RESORCE)) {
                    alien.getAlienStates().remove(MOVING_TO_RESORCE);
                    alien.getAlienStates().add(SEARCHING);
                }
            } else {

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


                    Point startPoint = new Point(required.getX(), required.getY());
                    Point basePoint = new Point(0, 0);

                    List<Point> route = TrackUtilities.findRoute(startPoint, basePoint, planet.getField());
                    alien.setRouteToBase(route);

                    routesToBase.put(startPoint, route);
                    return true;
                }
            }
        }
        return false;
    }

}
