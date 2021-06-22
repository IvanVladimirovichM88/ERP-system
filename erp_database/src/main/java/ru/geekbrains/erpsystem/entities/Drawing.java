package ru.geekbrains.erpsystem.entities;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Entity
@Table(name = "drawings")
@Data
public class Drawing {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "path")
    private String path;

    @OneToMany(mappedBy = "drawing", cascade = {CascadeType.REMOVE , CascadeType.REFRESH, CascadeType.PERSIST })
    List<DrawingThumbnail> thumbnails;

    @OneToOne(mappedBy = "drawing")
    private Unit unit;

}
