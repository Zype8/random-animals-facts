package com.michalk7.random_animals_facts.controller;

import com.michalk7.random_animals_facts.dto.GeneratorParametersDTO;
import com.michalk7.random_animals_facts.model.Fact;
import com.michalk7.random_animals_facts.service.FactsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/facts")
public class FactsController {

    private FactsService factsService;

    public FactsController(FactsService factsService) {
        this.factsService = factsService;
    }

    @PostMapping("/generate")
    public String generateFacts(@Valid @ModelAttribute("callApiForFacts")GeneratorParametersDTO generatorParametersDTO,
                                BindingResult bindingResult, Model model, RedirectAttributes redirectAttrs) {
        if(bindingResult.hasErrors()) {
            redirectAttrs.addFlashAttribute("generatorParameters", generatorParametersDTO);
            redirectAttrs.addFlashAttribute("org.springframework.validation.BindingResult.generatorParameters", bindingResult);
            return "redirect:/home";
        }

        String animalType = generatorParametersDTO.getAnimalType();
        int factsAmount = generatorParametersDTO.getFactsAmount();

        List<Fact> facts = factsService.callApiForFacts(animalType, factsAmount);

        model.addAttribute("factList", facts);

        return "facts-page";
    }

}
