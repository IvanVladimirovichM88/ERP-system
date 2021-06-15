package ru.geekbrains.erpsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.geekbrains.erpsystem.entities.OperationEntry;

import java.util.List;

public interface OperationEntryRepository extends JpaRepository<OperationEntry, Long> {

    List<OperationEntry> findAllByTechnologyId(Long id);
    void deleteByTechnologyId(Long id);

}
