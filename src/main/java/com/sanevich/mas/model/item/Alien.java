package com.sanevich.mas.model.item;

import com.sanevich.mas.pathfinding.Point;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Builder
@ToString(exclude = "routeToBase")
public class Alien extends Item {
    private int sizeOfBag;
    private Set<AlienState> alienStates;
    private int resourcesInBag;
    private String name;
    private List<Point> routeToBase;
}
