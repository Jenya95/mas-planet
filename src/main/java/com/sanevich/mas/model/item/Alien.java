package com.sanevich.mas.model.item;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Builder
@ToString
public class Alien extends Item {
    private int sizeOfBag;
    private Set<AlienState> alienStates;
}
