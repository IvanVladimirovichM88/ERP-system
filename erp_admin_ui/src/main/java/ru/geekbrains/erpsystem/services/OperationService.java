package ru.geekbrains.erpsystem.services;

import ru.geekbrains.erpsystem.data.OperationData;
import ru.geekbrains.erpsystem.entities.Operation;

import java.util.Optional;

public interface OperationService extends CrudService<OperationData,Long>{
    Optional<Operation> getByName(String opName);
}
