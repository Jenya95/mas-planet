package com.sanevich.mas.service;

import com.sanevich.mas.model.Cell;
import com.sanevich.mas.model.item.Alien;
import com.sanevich.mas.pathfinding.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sanevich.mas.model.item.AlienState.MAKE_A_STEP;
import static com.sanevich.mas.model.item.AlienState.MOVING_TO_BASE;
import static com.sanevich.mas.model.item.AlienState.MOVING_TO_RESOURCE;
import static com.sanevich.mas.service.Steps.didMakeStep;
import static com.sanevich.mas.service.Steps.moveAlien;

class MovingToBase {

    private static final Logger log = LoggerFactory.getLogger(MovingToBase.class);

    static void makeStepToBase(int y, int x, Cell[][] field) {
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
                //добавим в путь точку, на которой находится ресурс
            }
            //иначе это не первый шаг после того, как агент взял ресурс, значит точка на которой он стоит находится в
            //списке точек пути к базе, а следующая точка - следующая в этом списке
            else {
                int newPointIndex = alien.getRouteToBase().indexOf(new Point(x,y)) + 1;
                if (newPointIndex < alien.getRouteToBase().size() - 1) { //-1 чтобы не тронуть базу
                    moveAlien(field[y][x], alien, field[alien.getRouteToBase().get(newPointIndex).getyPosition()][alien.getRouteToBase().get(newPointIndex).getxPosition()]);
                }
                //агент дошел до базы
                else {
                    log.info("Alien {} dropped {} resources at base", alien.getName(), alien.getResourcesInBag());
                    alien.setResourcesInBag(0);
                    alien.getAlienStates().remove(MOVING_TO_BASE);
                    alien.getAlienStates().add(MOVING_TO_RESOURCE);
                    alien.getAlienStates().add(MAKE_A_STEP);
                }
            }
        }
    }
}
