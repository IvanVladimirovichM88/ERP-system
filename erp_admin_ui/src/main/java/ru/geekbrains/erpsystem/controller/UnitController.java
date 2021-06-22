package ru.geekbrains.erpsystem.controller;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.geekbrains.erpsystem.data.DrawingData;
import ru.geekbrains.erpsystem.data.UnitData;
import ru.geekbrains.erpsystem.data.UserData;
import ru.geekbrains.erpsystem.entities.Drawing;
import ru.geekbrains.erpsystem.service.FileStorageService;
import ru.geekbrains.erpsystem.services.DrawingService;
import ru.geekbrains.erpsystem.services.MaterialService;
import ru.geekbrains.erpsystem.services.UnitService;
import ru.geekbrains.erpsystem.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/units")
public class UnitController {

    private final UnitService unitService;
    private final MaterialService materialService;
    private final FileStorageService fileStorageService;
    private final DrawingService drawingService;
    private final UserService userService;


    public UnitController(UnitService unitService, MaterialService materialService, FileStorageService fileStorageService, DrawingService drawingService, UserService userService) {
        this.unitService = unitService;
        this.materialService = materialService;
        this.fileStorageService = fileStorageService;
        this.drawingService = drawingService;
        this.userService = userService;
    }

    @GetMapping("/all")
    public String showAll(
            Model model
    ){
        model.addAttribute("units", unitService.getAll() );
        return "units";
    }

    @GetMapping("/add")
    public String addNewUnit(
            Model model,
            Principal principal
    ){
        if (principal != null){
            model.addAttribute("userName", principal.getName());
        }else{
            return "redirect:/login";
        }

        model.addAttribute("unitData",new UnitData());
        model.addAttribute("allMaterials", materialService.getAll());
        model.addAttribute("allUnits", unitService.getAll());

        return "forms/unit_form";
    }

    @GetMapping("/add_form")
    public String addNewUnitForm(
            Model model,
            Principal principal
    ){

        model.addAttribute("unitData", new UnitData());
        if (principal != null){
            model.addAttribute("userName", principal.getName());
        }else{
            return "redirect:/login";
        }

        model.addAttribute("allMaterials", materialService.getAll());
        model.addAttribute("allUnits", unitService.getAll());

        return "forms/add_unit_form";
    }

    @PostMapping("/add")
    public String addNewUnit (
            @ModelAttribute UnitData unitData,
            @RequestParam("file")MultipartFile file,
            Principal principal,
            HttpServletRequest request
            )throws IOException {

        UserData constructor = userService.getByUserName(principal.getName());
        unitData.setConstrUserData(constructor);
        unitData.setAsm( !unitData.getIncludedUnitId().isEmpty() );

        String filename = unitData.getArt() + "--" + unitData.getName();


        if (file.getSize() == 0) {
            throw new FileUploadException("Файл не выбран или пустой");
        }
        if (!file.getContentType().equals("application/pdf")) {
            throw new FileUploadException("Неверный тип файла: " + file.getContentType());
        }
        if (filename.isBlank()) {
            throw new FileUploadException("Неверное имя файла (пустая строка)");
        }
        Optional<Drawing> drawing = drawingService.findByFileName(filename);
        if (drawing.isPresent()) {
            throw new FileUploadException(String.format("Чертеж %s уже загружен в хранилище. Перед загрузкой необходимо удалить существующий чертеж.", filename));
        }
        Pair<String, List<String>> keys = fileStorageService.uploadDrawing(file, filename);
        Drawing newDrawing = drawingService.saveDrawing(keys.getFirst(), keys.getSecond());

        unitData.setDrawingData(new DrawingData(newDrawing));

        unitService.insert(unitData);

        return "redirect:all";
    }


}
