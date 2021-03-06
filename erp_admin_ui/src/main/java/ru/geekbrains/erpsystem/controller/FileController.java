package ru.geekbrains.erpsystem.controller;

import javassist.NotFoundException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.geekbrains.erpsystem.entities.Drawing;
import ru.geekbrains.erpsystem.entities.DrawingThumbnail;
import ru.geekbrains.erpsystem.service.FileStorageService;
import ru.geekbrains.erpsystem.services.DrawingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/file_storage")
public class FileController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private DrawingService drawingService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam String filename,
                             HttpServletRequest request) throws IOException {
        if (file.getSize() == 0) {
            throw new FileUploadException("Файл не выбран или пустой");
        }
        if (!file.getContentType().equals("application/pdf")) {
            throw new FileUploadException("Неверный тип файла: " + file.getContentType());
        }
        if (filename.isBlank()) {
            throw new FileUploadException("Неверное имя файла (пустая строка)");
        }
        Optional<Drawing> drawing = drawingService.findByFileName(filename);
        if (drawing.isPresent()) {
            throw new FileUploadException(String.format("Чертеж %s уже загружен в хранилище. Перед загрузкой необходимо удалить существующий чертеж.", filename));
        }
        Pair<String, List<String>> keys = fileStorageService.uploadDrawing(file, filename);
        drawingService.saveDrawing(keys.getFirst(), keys.getSecond());
        return "redirect:" + request.getHeader("referer");
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] downloadFile(@RequestParam("id") Long id) throws NotFoundException, IOException {
        byte[] data;
        Drawing drawing = drawingService.findById(id);
        String path = drawing.getPath();
        data = fileStorageService.downloadDrawing(path);
        return data;
    }

    @PostMapping("/delete")
    public String deleteFile(@RequestParam Long id, HttpServletRequest request) throws NotFoundException, FileNotFoundException {
        Drawing drawing = drawingService.deleteDrawingAndThumbnails(id);

        String pdfKey = drawing.getPath();
        List<String> thumbKeys = drawing.getThumbnails().stream()
                .map(DrawingThumbnail::getPath)
                .collect(Collectors.toList());

        fileStorageService.deleteDrawing(pdfKey, thumbKeys);
        return "redirect:" + request.getHeader("referer");
    }

    @GetMapping("/list")
    public String listFiles(@RequestParam("pattern") String pattern, Model model) {
        List<Drawing> drawings = drawingService.findAllByPathContaining(pattern);
        model.addAttribute("drawings", drawings);
        logger.info(drawings.stream().map(Drawing::getPath).collect(Collectors.toList()).toString());
        return "files";
    }

    @GetMapping("/thumb/{id}")
    public void getThumbnail(@PathVariable("id") Long id, HttpServletResponse response) {
        try {
            DrawingThumbnail thumb = drawingService.findThumbnailById(id);
            String path = thumb.getPath();
            byte[] img = fileStorageService.downloadDrawing(path);
            response.setContentType("image/png");
            response.getOutputStream().write(img);
        } catch (NotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

}
