package com.sanevich.mas.model.item;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
//@ToString
public class Resource extends Item {
    private int size;
    private String name;

    @Override
    public String toString() {
        return String.valueOf(size + " " + name);
    }
}
