package com.sanevich.mas.model.item;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Builder
public class Alien extends Item {
    private int sizeOfBag;
    private AlienState alienState;
}
