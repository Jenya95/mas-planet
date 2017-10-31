package com.sanevich.mas.model;

import com.sanevich.mas.core.CommonData;
import com.sanevich.mas.model.item.Item;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class Planet {
    private Cell[][] field;

    public void initializeFiled() {
        Arrays.stream(field)
                .forEach(x -> {
                    for (int i = 0; i < CommonData.WIDTH_MAP; i++) {
                        x[i] = new Cell();
                    }
                });
    }

    public void addItemOnField(Item item, int x, int y) {
        field[x][y].setItem(item);
    }
}