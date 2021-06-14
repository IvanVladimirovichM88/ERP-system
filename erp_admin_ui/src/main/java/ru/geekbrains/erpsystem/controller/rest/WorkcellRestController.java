package ru.geekbrains.erpsystem.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.geekbrains.erpsystem.data.WorkcellData;
import ru.geekbrains.erpsystem.entities.Workcell;
import ru.geekbrains.erpsystem.services.WorkcellService;

import java.util.List;

@RestController()
@RequestMapping("/api/workcells")
@CrossOrigin
public class WorkcellRestController {

    @Autowired
    private WorkcellService workcellService;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WorkcellData> getWorkcells() {
        return workcellService.getAll();
    }

}
