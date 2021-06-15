package ru.geekbrains.erpsystem.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.geekbrains.erpsystem.data.OperationData;
import ru.geekbrains.erpsystem.services.OperationService;

import java.util.List;

@RestController
@RequestMapping("/api/operations")
@CrossOrigin
public class OperationRestController {

    @Autowired
    OperationService operationService;

    @GetMapping("")
    public List<OperationData> getAllOperations() {
        return operationService.getAll();
    }

}
