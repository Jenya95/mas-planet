package com.sanevich.mas.core;

import com.sanevich.mas.model.Cell;
import com.sanevich.mas.model.item.Alien;
import com.sanevich.mas.model.item.Resource;
import com.sanevich.mas.pathfinding.Point;

import java.util.Random;

import static com.sanevich.mas.model.item.AlienState.MOVING_TO_BASE;
import static com.sanevich.mas.model.item.AlienState.MOVING_TO_RESOURCE;
import static com.sanevich.mas.model.item.AlienState.SEARCHING;
import static com.sanevich.mas.core.Steps.*;

class MovingToResource {
    static void makeStepToResource(int y, int x, Cell[][] field) {
        //шаг только если при этой итерации еще не был сделан шаг
        if (!didMakeStep(field[y][x])) {
            Alien alien = (Alien) field[y][x].getItem();
            //агент идет назад по пути до ресурса, пока не встретится с ресурсом
            int newPointIndex = alien.getRouteToBase().indexOf(new Point(x, y)) - 1;

            if (newPointIndex >= 0 && (newPointIndex != alien.getRouteToBase().size()-2)) {
                moveAlien(field[y][x], alien, field[alien.getRouteToBase().get(newPointIndex).getyPosition()][alien.getRouteToBase().get(newPointIndex).getxPosition()]);
            } else if (newPointIndex==-2 || (newPointIndex == alien.getRouteToBase().size()-2)) {
                alien.getAlienStates().remove(MOVING_TO_RESOURCE);
                alien.getAlienStates().add(SEARCHING);
            }

            //когда он дошел до ресура, то должен опять наполнить свой рюкзак и отправиться на базу
            if (alien.getRouteToBase().get(0).equals(new Point(x, y))) {
                int lastIndex = alien.getRouteToBase().size()-1;
                int yPosRes = alien.getRouteToBase().get(lastIndex).getyPosition();
                int xPosRes = alien.getRouteToBase().get(lastIndex).getxPosition();

                if (field[yPosRes][xPosRes].getItem() instanceof Resource) {
                    //агент возле ресурса
                    Resource resource = (Resource) field[yPosRes][xPosRes].getItem();
                    alien.getAlienStates().remove(MOVING_TO_RESOURCE);
                    if (resource != null && resource.getSize() > 0) {
                        collectResource(alien, resource);
                        alien.getAlienStates().add(MOVING_TO_BASE);
                    } else {
                        alien.getAlienStates().add(SEARCHING);
                    }
                } else {
                    //агент встал на клетку, на которой был ресурс
                    alien.getAlienStates().remove(MOVING_TO_RESOURCE);
                    alien.getAlienStates().add(SEARCHING);
                }
            }
        }
    }
}
