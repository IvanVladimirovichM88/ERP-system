package ru.geekbrains.erpsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.erpsystem.data.MaterialData;
import ru.geekbrains.erpsystem.services.MaterialService;

@Controller
@RequestMapping("/materials")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping("/all")
    public String showAllMaterials(
            Model model
    ){
        model.addAttribute("materials", materialService.getAll());
        return "materials";
    }

    @GetMapping("/edit/{id}")
    public String editMaterial(
            @PathVariable Long id,
            Model model
    ){
        model.addAttribute("form_name", "Edit Material");

        MaterialData materialData = materialService.getById(id)
                .orElseThrow(()-> new RuntimeException("Material with id - "+ id +"not found"));
        model.addAttribute("materialData", materialData);
        return "forms/material_form";
    }

    @GetMapping("/add")
    public String addNewMaterial(
            Model model
    ){
        model.addAttribute("form_name", "New Material");
        model.addAttribute("materialData", new MaterialData());
        return "forms/material_form";
    }

    @PostMapping("/add")
    public String addAndUpdateMaterial(
            @ModelAttribute MaterialData materialData
    ){
        if(materialData.getId() == null){
            materialService.insert(materialData);
        }else{
            if(materialService.getById(materialData.getId()).isEmpty()){
                throw new RuntimeException("Material with id - "+materialData.getId()+" not found");
            }
            materialService.update(materialData);
        }

        return "redirect:all";
    }

    @GetMapping("/delete/{id}")
    public String removeMaterial(
            @PathVariable Long id
    ){
        materialService.delete(id);
        return "redirect:/materials/all";
    }

}
