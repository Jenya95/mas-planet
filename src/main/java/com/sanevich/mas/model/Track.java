package com.sanevich.mas.model;

import com.sanevich.mas.pathfinding.Point;
import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class Track {
    private Point startPoint;
    private Point endPoint;
    private List<Point> route;
}
