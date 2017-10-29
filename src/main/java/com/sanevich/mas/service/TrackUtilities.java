package com.sanevich.mas.service;

import com.sanevich.mas.model.Cell;
import com.sanevich.mas.pathfinding.ExampleFactory;
import com.sanevich.mas.pathfinding.Point;
import com.sanevich.mas.pathfinding.Map;

import java.util.List;

public class TrackUtilities {
    public static List<Point> findRoute(Point startPoint, Point endPoint, Cell[][] map) {
        Map<Point> myMap = new Map<>(map.length, map[0].length, new ExampleFactory());
        List<Point> path = myMap.findPath(startPoint.getxPosition(),
                startPoint.getyPosition(),
                endPoint.getxPosition(),
                endPoint.getyPosition());

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (path!= null && path.contains(new Point(i,j))) {
                    map[i][j].setPath(true);
                }
            }
        }

        return path;
    }
}
