package com.sanevich.mas.model;

import com.sanevich.mas.model.item.Alien;
import com.sanevich.mas.model.item.Base;
import com.sanevich.mas.model.item.Resource;
import lombok.*;
import com.sanevich.mas.model.item.Item;
import lombok.experimental.Tolerate;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Cell {
    private int x;
    private int y;
    private Item item;
    private boolean isPath = false;

    @Tolerate
    public Cell(Cell cell) {
        this.item = cell.item;
        this.x = cell.getX();
        this.y = cell.getY();
        this.isPath = cell.isPath;
    }

    public boolean isResource() {
        return item instanceof Resource;
    }

    public boolean isAlien() {
        return item instanceof Alien;
    }

    public boolean isBase() {
        return item instanceof Base;
    }
}