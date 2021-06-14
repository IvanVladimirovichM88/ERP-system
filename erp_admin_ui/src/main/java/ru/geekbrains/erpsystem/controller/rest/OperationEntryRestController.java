package ru.geekbrains.erpsystem.controller.rest;

import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.erpsystem.data.OperationEntryData;
import ru.geekbrains.erpsystem.services.impl.OperationEntryServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/operation_entries")
@CrossOrigin
public class OperationEntryRestController {

    @Autowired
    private OperationEntryServiceImpl operationEntryService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/get")
    public OperationEntryData get(@RequestParam("id") Long id) throws NotFoundException {
        return operationEntryService.getById(id).orElse(null);
    }

    @GetMapping("")
    public List<OperationEntryData> get() {
        return operationEntryService.getAll();
    }

    @PostMapping(value = "/post", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void post(@RequestBody OperationEntryData data) {
        logger.info("Принят объект: " + data.toString());
        operationEntryService.insert(data);
    }

}
