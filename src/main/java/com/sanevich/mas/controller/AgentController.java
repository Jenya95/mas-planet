package com.sanevich.mas.controller;

import com.sanevich.mas.model.Cell;
import com.sanevich.mas.model.Planet;
import com.sanevich.mas.model.item.Alien;
import com.sanevich.mas.model.item.Base;
import com.sanevich.mas.model.item.Resource;
import com.sanevich.mas.service.CommonData;
import com.sanevich.mas.service.Steps;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class AgentController {

    private static Planet planet;

    @PostConstruct
    public void init() throws IOException, InterruptedException {
        initMap();
    }

    private static void initMap() {
        planet = Planet.builder()
                .field(new Cell[CommonData.HEIGHT_MAP][CommonData.WIDTH_MAP])
                .build();

        Base base = new Base();

        Alien a1 = Alien.builder().sizeOfBag(3).build();
        Alien a2 = Alien.builder().sizeOfBag(2).build();

        Resource r1 = new Resource(10);

        Arrays.stream(planet.getField())
                .forEach(x -> {
                    for (int i = 0; i < CommonData.WIDTH_MAP; i++) {
                        x[i] = new Cell();
                    }
                });

        planet.getField()[0][0].setItem(base);
        planet.getField()[5][10].setItem(a1);
        planet.getField()[7][13].setItem(a2);
        planet.getField()[10][5].setItem(r1);
    }

    @GetMapping("table")
    public String showTable(Model model) throws IOException {
        Steps.doStep(planet);

        List<List<Cell>> list = Arrays.stream(planet.getField())
                .map(Arrays::asList)
                .collect(Collectors.toList());


        model.addAttribute("planetField", list);
        return "agentPage :: planet";
    }
}
