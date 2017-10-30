package com.sanevich.mas.service;

import com.sanevich.mas.model.*;
import com.sanevich.mas.model.item.*;
import com.sanevich.mas.pathfinding.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.sanevich.mas.model.item.AlienState.*;
import static com.sanevich.mas.service.MovingToBase.makeStepToBase;
import static com.sanevich.mas.service.SearchingSteps.makeSimpleStep;

public class Steps {

    private static final Logger log = LoggerFactory.getLogger(Steps.class);

    private static int stepCount = 0;

    private static Planet planet;
    private static Map<Point,List<Point>> routesToBase = new HashMap<>();
    private static Set<Resource> resources = new HashSet<>();

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
                    } else
                    //если в данной ячейке агент в состоянии MOVING_TO_RESOURCE - то он должен двигаться обратно в
                    //сторону ресурса
                    if (((Alien) planet.getField()[i][j].getItem()).getAlienStates().contains(MOVING_TO_RESOURCE)) {
                        makeStepToResource(i, j, planet.getField());
                    }
                } else if (planet.getField()[i][j].getItem() instanceof Resource) {
                    resources.add((Resource) planet.getField()[i][j].getItem());

                    Integer sumOfResources = resources
                            .stream()
                            .map(Resource::getSize)
                            .reduce(0, (x, y) -> x + y);

                    if (sumOfResources == 0) {
                        log.info("All resources collected!");
                        CommonData.goalAchieved = true;
                        break;
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

    private static void makeStepToResource(int y, int x, Cell[][] field) {
        //шаг только если при этой итерации еще не был сделан шаг
        if (!didMakeStep(field[y][x])) {
            Alien alien = (Alien) field[y][x].getItem();
            //агент идет назад по пути до ресурса, пока не встретится с ресурсом
            int newPointIndex = alien.getRouteToBase().indexOf(new Point(x, y)) - 1;

            if (newPointIndex >= 0) {
                moveAlien(field[y][x], alien, field[alien.getRouteToBase().get(newPointIndex).getyPosition()][alien.getRouteToBase().get(newPointIndex).getxPosition()]);
            }

            //когда он дошел до ресура, то должен опять наполнить свой рюкзак и отправиться на базу
            if (alien.getRouteToBase().get(0).equals(new Point(x, y))) {
                int lastIndex = alien.getRouteToBase().size()-1;
                Resource resource = (Resource) field[alien.getRouteToBase().get(lastIndex).getyPosition()][alien.getRouteToBase().get(lastIndex).getxPosition()].getItem();
                alien.getAlienStates().remove(MOVING_TO_RESOURCE);
                if (resource.getSize() > 0) {
                    collectResource(alien, resource);
                    alien.getAlienStates().add(MOVING_TO_BASE);
                } else {
                    alien.getAlienStates().add(SEARCHING);
                }
            }
        }
    }

    public static void moveAlien(Cell cellAlien, Alien alien, Cell nextCellAlien) {
        Cell cell = new Cell(nextCellAlien);
        alien.getAlienStates().add(MAKE_A_STEP);
        nextCellAlien.setItem(alien);
        cellAlien.setItem(cell.getItem());
    }

    public static boolean didMakeStep(Cell cell) {
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

    public static boolean checkIfResource(Cell current, Cell required) {
        if (required.getItem() instanceof Resource) {

            Alien alien = (Alien) current.getItem();

            //если агент шел к этому ресурсу, а он теперь пустой - то агент переходит в состояние поиска нового ресурса
            if (((Resource) required.getItem()).getSize() == 0) {
                if (alien.getAlienStates().contains(MOVING_TO_RESOURCE)) {
                    alien.getAlienStates().remove(MOVING_TO_RESOURCE);
                    alien.getAlienStates().add(SEARCHING);
                }
            } else {

                Resource resource = (Resource) required.getItem();

                alien.getAlienStates().remove(SEARCHING);
                alien.getAlienStates().add(MOVING_TO_BASE);

                if (resource.getSize() > 0) {
                    collectResource(alien, resource);

                    Point startPoint = new Point(required.getX(), required.getY());
                    Point basePoint = new Point(0, 0);

                    List<Point> route = TrackUtilities.findRoute(startPoint, basePoint, planet.getField());
                    route.add(new Point(required.getX(), required.getY()));
                    alien.setRouteToBase(route);

                    routesToBase.put(startPoint, route);
                    return true;
                }
            }
        }
        return false;
    }

    static void collectResource(Alien alien, Resource resource) {
        if (resource.getSize() > alien.getSizeOfBag()) {
            resource.setSize(resource.getSize() - alien.getSizeOfBag());
            alien.setResourcesInBag(alien.getSizeOfBag());
        } else {
            alien.setResourcesInBag(resource.getSize());
            resource.setSize(0);
        }
        log.info("Alien {} collect {} from resource {} and move to base", alien.getName(),
                alien.getResourcesInBag(), resource.getName());
    }


    static int getStepCount() {
        return stepCount;
    }

    public static void setStepCount(int stepCount) {
        Steps.stepCount = stepCount;
    }


}
