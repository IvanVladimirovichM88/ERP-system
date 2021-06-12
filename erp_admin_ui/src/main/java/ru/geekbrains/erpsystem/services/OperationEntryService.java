package ru.geekbrains.erpsystem.services;

import ru.geekbrains.erpsystem.data.OperationEntryData;

import java.util.List;

public interface OperationEntryService extends CrudService<OperationEntryData,Long> {

    List<OperationEntryData> findAllById(List<Long> ids);

    List<OperationEntryData> findAllByTechnologyId(Long id);

    void deleteByTechnologyId(Long id);

}
