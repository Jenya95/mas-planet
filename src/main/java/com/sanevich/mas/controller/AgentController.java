package com.sanevich.mas.controller;

import com.sanevich.mas.model.Cell;
import com.sanevich.mas.model.Planet;
import com.sanevich.mas.core.CommonData;
import com.sanevich.mas.core.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class AgentController {

    private final Planet planet;

    @Autowired
    public AgentController(Planet planet) {
        this.planet = planet;
    }

    @GetMapping("generate")
    public String showTable(Model model) throws IOException {
        String msg = "";

        if (!CommonData.goalAchieved) {
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

        //todo generate some agents and resources and put them to the random places

        redirectAttributes.addFlashAttribute("start", true);
        return "redirect:/generate";
    }
}
