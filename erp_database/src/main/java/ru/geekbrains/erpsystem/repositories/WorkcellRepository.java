package ru.geekbrains.erpsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.geekbrains.erpsystem.entities.Workcell;

import java.util.Optional;

public interface WorkcellRepository extends JpaRepository<Workcell, Long> {
    Optional<Workcell> findByName(String name);
}
