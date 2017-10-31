package com.sanevich.mas.controller;

import com.sanevich.mas.model.Cell;
import com.sanevich.mas.model.Planet;
import com.sanevich.mas.core.CommonData;
import com.sanevich.mas.core.Steps;
import com.sanevich.mas.model.item.Alien;
import com.sanevich.mas.model.item.Base;
import com.sanevich.mas.model.item.Item;
import com.sanevich.mas.model.item.Resource;
import com.sanevich.mas.pathfinding.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sun.management.Agent;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.sanevich.mas.core.CommonData.*;
import static com.sanevich.mas.core.Steps.setResources;
import static com.sanevich.mas.core.Steps.setRoutesToBase;
import static com.sanevich.mas.core.Steps.setStepCount;

@Controller
@RequestMapping("/")
public class AgentController {

    private final Planet planet;

    @Autowired
    public AgentController(Planet planet) {
        this.planet = planet;
    }

    @GetMapping("generate")
    public String showTable(Model model) {
        String msg = "";

        if (!goalAchieved) {
            Steps.doStep(planet);
        } else {
            msg = "Цель достигнута! Все ресурсы собраны!";
        }

        List<List<Cell>> list = Arrays.stream(planet.getField())
                .map(Arrays::asList)
                .collect(Collectors.toList());

        if (!msg.isEmpty()) {
            model.addAttribute("msg", msg);
        }

        model.addAttribute("planetField", list);
        return "agentPage :: planet";
    }

    @GetMapping
    public String showTableInit(Model model) throws IOException {

        List<List<Cell>> list = Arrays.stream(planet.getField())
                .map(Arrays::asList)
                .collect(Collectors.toList());

        model.addAttribute("planetField", list);
        return "agentPage";
    }

    @PostMapping("start-new-planet")
    public String showNewTable(RedirectAttributes redirectAttributes,
                               @RequestParam(value = "numOfAgents") Integer numOfAgents,
                               @RequestParam(value = "numOfResources") Integer numOfResources) throws ParseException {

        planet.initializeFiled();
        Random rand = new Random();

        for (int i = 0; i < numOfAgents; i++) {
            Alien alien = Alien.builder()
                    .sizeOfBag(rand.nextInt(5) + 1)
                    .name("a" + i)
                    .alienStates(new HashSet<>())
                    .build();

            putItemInRandomCell(rand, alien);
        }

        for (int i = 0; i < numOfResources; i++) {
            Resource resource = new Resource(rand.nextInt(25) + 1, "r"+i);

            putItemInRandomCell(rand, resource);
        }

        redirectAttributes.addFlashAttribute("start", true);
        return "redirect:/";
    }

    private void putItemInRandomCell(Random rand, Item item) {
        int x = rand.nextInt(HEIGHT_MAP);
        int y = rand.nextInt(WIDTH_MAP);
        try {
            while (true) {
                if (planet.getField()[x][y].getItem() == null) {
                    planet.addItemOnField(item, x, y);
                    break;
                } else {
                    x = rand.nextInt(HEIGHT_MAP);
                    y = rand.nextInt(WIDTH_MAP);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println(x + "," + y);
            e.printStackTrace();
        }
    }
}
