package ru.geekbrains.erpsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.geekbrains.erpsystem.services.TechnologyService;
import ru.geekbrains.erpsystem.services.UserService;

@Controller
@RequestMapping("/technologies")
public class TechnologyController {

    @Value("${application.url}")
    private String applicationUrl;

    @Autowired
    TechnologyService technologyService;
    @Autowired
    UserService userService;

    @GetMapping("/all")
    public String getAllTechnologies(Model m) {
        m.addAttribute("technologies", technologyService.getAll());
        return "technologies";
    }

    @GetMapping("/edit/{id}")
    public String showEditTechnologyPage(@PathVariable("id") Long id, Model m) {
        m.addAttribute("applicationUrl", applicationUrl);
        m.addAttribute("technologyId", id);
        m.addAttribute("technologistId", "");
        return "forms/edit_technology_form";
    }

    @GetMapping("add")
    public String showAddTechnologyPage(Model m, Authentication auth) {
        m.addAttribute("applicationUrl", applicationUrl);
        m.addAttribute("technologyId", "");
        //Здесь можно распаковать Optional без проверки: User точно существует, поскольку авторизован.
        m.addAttribute("technologistId", userService.getByName(auth.getName()).get().getId());
        return "forms/edit_technology_form";
    }

    @GetMapping("/delete/{id}")
    public String deleteTechnologyPage(@PathVariable("id") Long id) {
        technologyService.delete(id);
        return  "redirect:/technologies/all";
    }

}
