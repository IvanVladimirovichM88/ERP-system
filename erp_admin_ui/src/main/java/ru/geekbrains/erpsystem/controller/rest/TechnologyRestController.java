package ru.geekbrains.erpsystem.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.erpsystem.data.TechnologyData;
import ru.geekbrains.erpsystem.services.TechnologyService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/technology")
@CrossOrigin
public class TechnologyRestController {

    @Autowired
    TechnologyService technologyService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/get/{id}")
    TechnologyData getById(@PathVariable("id") Long id) {
        Optional<TechnologyData> data = technologyService.getById(id);
        if (data.isEmpty()) {
            logger.error("Не найдена технология с id = " + id);
            return new TechnologyData();
        }
        return data.get();
    }

    @GetMapping("/get")
    List<TechnologyData> getAll() {
        return technologyService.getAll();
    }


    @PostMapping("/post")
    public TechnologyData post(@RequestBody TechnologyData data) {
        logger.info("Получены данные: " + data.toString());
        TechnologyData response = technologyService.insert(data);
        logger.info("Response object: " + response.toString());
        return response;
    }

}
