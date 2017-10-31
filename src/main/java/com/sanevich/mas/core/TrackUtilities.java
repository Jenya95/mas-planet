package com.sanevich.mas.core;

import com.sanevich.mas.model.Cell;
import com.sanevich.mas.pathfinding.AstarMap;
import com.sanevich.mas.pathfinding.ExampleFactory;
import com.sanevich.mas.pathfinding.Point;

import java.util.List;

public class TrackUtilities {
    public static List<Point> findRoute(Point startPoint, Point endPoint, Cell[][] map) {
        AstarMap<Point> myAstarMap = new AstarMap<>(map[0].length, map.length, new ExampleFactory());
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
