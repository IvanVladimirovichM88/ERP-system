package ru.geekbrains.erpsystem.services;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.erpsystem.entities.Drawing;
import ru.geekbrains.erpsystem.entities.DrawingThumbnail;
import ru.geekbrains.erpsystem.repositories.DrawingRepository;
import ru.geekbrains.erpsystem.repositories.DrawingThumbnailRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DrawingService {

    @Autowired
    DrawingRepository drawingRepository;
    @Autowired
    DrawingThumbnailRepository drawingThumbnailRepository;

    public Drawing saveDrawing(String path, List<String> thumbPaths) {
        Drawing drawing = new Drawing();
        drawing.setPath(path);
        drawing.setUnit(null);

        List<DrawingThumbnail> thumbs =  thumbPaths.stream()
                .map(thumbPath -> {
                    DrawingThumbnail thumb = new DrawingThumbnail();
                    thumb.setPath(thumbPath);
                    thumb.setDrawing(drawing);
                    return thumb;
                })
                .collect(Collectors.toList());


        drawingThumbnailRepository.saveAll(thumbs);

        return drawingRepository.save(drawing);
    }

    public Drawing deleteDrawingAndThumbnails(Long drawingId) throws NotFoundException {
        Drawing drawing = findById(drawingId);
        drawingRepository.deleteById(drawingId);
        return drawing;
    }

    public Drawing findById(Long id) throws NotFoundException {
        return drawingRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Чертеж с id = %s не найден в базе данных", id))
        );
    }

    public List<Drawing> findAll() {
        return drawingRepository.findAll();
    }

    public List<Drawing> findAllByPathContaining(String pattern) {
        if (pattern.equals("*")) {
            return drawingRepository.findAll();
        }
        return drawingRepository.findAllByPathContaining(pattern);
    }

    public Optional<Drawing> findByFileName(String filename) {
        return drawingRepository.findByPath("drawings/" + filename + ".pdf");
    }

    public DrawingThumbnail findThumbnailById(Long id) throws NotFoundException {
        return drawingThumbnailRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Thumbnail with id = %d not found", id))
        );
    }

    public String getFileName(Drawing drawing) {
        String path = drawing.getPath();
        String[] parts = path.split("/");
        String filename = parts[parts.length - 1];
        return filename.substring(0, filename.length() - 4);
    }

}
