package ru.geekbrains.erpsystem.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "technology")
@Data
public class Technology {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "technologist", nullable = false)
    private User technologist;

    @OneToMany(mappedBy = "technology")
    List<OperationEntry> operationEntries = new ArrayList<>();

}
