package com.sanevich.mas.model;

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
    private boolean isPath;

    @Tolerate
    public Cell(Cell cell) {
        this.item = cell.item;
        this.x = cell.getX();
        this.y = cell.getY();
        this.isPath = cell.isPath;
    }
}