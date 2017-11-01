package com.sanevich.mas.core;

import com.sanevich.mas.model.Cell;
import com.sanevich.mas.model.item.Resource;
import com.sanevich.mas.pathfinding.AstarMap;
import com.sanevich.mas.pathfinding.ExampleFactory;
import com.sanevich.mas.pathfinding.Point;

import java.util.List;

import static com.sanevich.mas.core.Steps.getPlanet;

public class TrackUtilities {
    public static List<Point> findRoute(Point startPoint, Point endPoint, Cell[][] map) {
        AstarMap<Point> myAstarMap = new AstarMap<>(map[0].length, map.length, new ExampleFactory());

        for (int i = 0; i < getPlanet().getField().length; i++) {
            for (int j = 0; j < getPlanet().getField()[i].length; j++) {
                if (getPlanet().getField()[i][j].getItem() instanceof Resource) {
                    myAstarMap.setWalkable(j,i,false);
                }
            }
        }

        try {
            List<Point> path = myAstarMap.findPath(startPoint.getxPosition(),
                    startPoint.getyPosition(),
                    endPoint.getxPosition(),
                    endPoint.getyPosition());

            return path;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(startPoint.toString() + endPoint.toString());
        }

        return null;
    }
}
