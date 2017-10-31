package com.sanevich.mas.core;

import com.sanevich.mas.model.*;
import com.sanevich.mas.model.item.*;
import com.sanevich.mas.pathfinding.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.sanevich.mas.core.CommonData.*;
import static com.sanevich.mas.model.item.AlienState.*;
import static com.sanevich.mas.core.MovingToBase.makeStepToBase;
import static com.sanevich.mas.core.MovingToResource.makeStepToResource;
import static com.sanevich.mas.core.SearchingSteps.makeSimpleStep;

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

    static void moveAlien(Cell cellAlien, Alien alien, Cell nextCellAlien) {
        Cell cell = new Cell(nextCellAlien);
        alien.getAlienStates().add(MAKE_A_STEP);
        nextCellAlien.setItem(alien);
        cellAlien.setItem(cell.getItem());
    }

    static boolean didMakeStep(Cell cell) {
        return ((Alien) cell.getItem())
                .getAlienStates().contains(MAKE_A_STEP);
    }

    static boolean checkIfPath(Cell current, Cell required) {
        if (required.isPath()) {
            Alien alien = (Alien) current.getItem();
            List<Point> pathToBase = routesToBase
                    .values()
                    .stream()
                    .filter(x -> x.contains(new Point(required.getX(), required.getY())))
                    .findFirst()
                    .orElse(null);
            alien.setRouteToBase(pathToBase);
            alien.getAlienStates().remove(SEARCHING);
            alien.getAlienStates().add(MOVING_TO_RESOURCE);
            alien.getAlienStates().add(MAKE_A_STEP);

            Cell buff = new Cell(current);
            planet.getField()[current.getY()][current.getX()].setItem(required.getItem());
            planet.getField()[required.getY()][required.getX()].setItem(buff.getItem());
            return true;
        }
        return false;
    }

    static boolean checkIfResource(Cell current, Cell required) {
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
                    Point basePoint = new Point(X_BASE_COORDINATE, Y_BASE_COORDINATE);

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

    static void clearPathOnScreen(Alien alien) {
        for (Point point: alien.getRouteToBase()) {
            planet.getField()[point.getyPosition()][point.getxPosition()].setPath(false);
        }

        //уберем тогда же ресурс с карты
        planet.getField()[alien.getRouteToBase().get(alien.getRouteToBase().size()-1).getyPosition()][alien
                .getRouteToBase().get(alien.getRouteToBase().size()-1).getxPosition()].setItem(null);

        Integer sumOfResources = resources
                .stream()
                .map(Resource::getSize)
                .reduce(0, (x, y) -> x + y);

        if (sumOfResources == 0) {
            log.info("All resources collected!");
            goalAchieved = true;
        }
    }

    static void collectResource(Alien alien, Resource resource) {
        if (resource.getSize() > alien.getSizeOfBag()) {
            resource.setSize(resource.getSize() - alien.getSizeOfBag());
            alien.setResourcesInBag(alien.getSizeOfBag());
        } else {
            alien.setResourcesInBag(resource.getSize());
            resource.setSize(0);
            alien.getAlienStates().add(LAST_MOVE_TO_BASE);
        }
        log.info("Alien {} collect {} from resource {} and move to base", alien.getName(),
                alien.getResourcesInBag(), resource.getName());
    }


    static int getStepCount() {
        return stepCount;
    }
}
