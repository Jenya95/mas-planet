package com.sanevich.mas.model.item;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class Base extends Item {
    @Override
    public String toString() {
        return "B";
    }
}
