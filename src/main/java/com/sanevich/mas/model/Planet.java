package com.sanevich.mas.model;

import com.sanevich.mas.core.CommonData;
import com.sanevich.mas.model.item.Base;
import com.sanevich.mas.model.item.Item;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static com.sanevich.mas.core.CommonData.X_BASE_COORDINATE;
import static com.sanevich.mas.core.CommonData.Y_BASE_COORDINATE;
import static com.sanevich.mas.core.CommonData.goalAchieved;
import static com.sanevich.mas.core.Steps.setResources;
import static com.sanevich.mas.core.Steps.setRoutesToBase;
import static com.sanevich.mas.core.Steps.setStepCount;

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
        Base base = new Base();
        this.addItemOnField(base, X_BASE_COORDINATE, Y_BASE_COORDINATE);
        goalAchieved = false;
        setStepCount(0);
        setResources(new HashSet<>());
        setRoutesToBase(new HashMap<>());

    }

    public void addItemOnField(Item item, int x, int y) {
        field[x][y].setItem(item);
    }
}