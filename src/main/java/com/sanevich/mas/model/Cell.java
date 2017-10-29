package com.sanevich.mas.model;

import lombok.*;
import com.sanevich.mas.model.item.Item;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Cell {
    private int x;
    private int y;
    private Item item;
}