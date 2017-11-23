package com.sanevich.mas.model.item;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class Base extends Item {
    private int size;

    @Override
    public String toString() {
        return String.valueOf(size);
    }

    public void increaseSize(int size) {
        this.size += size;
    }
}
