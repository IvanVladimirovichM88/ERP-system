package ru.geekbrains.erpsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.erpsystem.data.OperationData;
import ru.geekbrains.erpsystem.data.WorkcellData;
import ru.geekbrains.erpsystem.services.WorkcellService;

@Controller
@RequestMapping("/workcells")
public class WorkcellController {

    @Autowired
    private WorkcellService workcellService;

    @GetMapping("/all")
    public String showAllOperations(
            Model model
    ){
        model.addAttribute("workcells", workcellService.getAll());
        return "workcells";
    }

    @GetMapping("/edit/{id}")
    public String editWorkcell(
            @PathVariable Long id,
            Model model
    ){
        model.addAttribute("form_name","Edit workcell");
        WorkcellData workcellData = workcellService.getById(id)
                .orElseThrow(()->new RuntimeException("Workcell with id - "+id+ " not found"));
        model.addAttribute("workcellData", workcellData);
        return "forms/workcell_form";
    }

    @GetMapping("/add")
    public String addNewWorkcell(
            Model model
    ){
        model.addAttribute("form_name","New workcell");
        model.addAttribute("workcellData", new WorkcellData());
        return "forms/workcell_form";
    }

    @PostMapping("/add")
    public String addAndUpdateWorkcell(
            @ModelAttribute WorkcellData workcellData
    ){
        if (workcellData.getId() == null){
            workcellService.insert(workcellData);
        } else {
            workcellService.update(workcellData);
        }
        return "redirect:all";
    }

    @GetMapping("/delete/{id}")
    public String deleteWorkcellById(
            @PathVariable Long id,
            Model model
    ){
        workcellService.delete(id);
        return "redirect:all";
    }

}
