package ru.geekbrains.erpsystem.services;

import ru.geekbrains.erpsystem.data.WorkcellData;
import ru.geekbrains.erpsystem.entities.Workcell;

import java.util.Optional;

public interface WorkcellService extends CrudService<WorkcellData, Long> {

    Optional<Workcell> getByName(String name);

}
