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
        return path;
    }
}
