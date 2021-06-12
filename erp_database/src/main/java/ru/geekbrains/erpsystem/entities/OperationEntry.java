package ru.geekbrains.erpsystem.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "operation_entries")
@Data
public class OperationEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "workcell_id")
    private Workcell workcell;

    @ManyToOne(optional = false)
    @JoinColumn(name = "operation_id", nullable = false)
    private Operation operation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "technology_id", nullable = false)
    private Technology technology;

    @Column(name = "params")
    private String params;

    @Column(name = "turn", nullable = false)
    Integer turn;

    @Column(name = "duration")
    Float duration;

    @Column(name = "qty")
    Integer qty;

    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

}
