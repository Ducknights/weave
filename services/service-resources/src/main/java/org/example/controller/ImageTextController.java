package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.entity.ImageText;
import org.example.service.ImageTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin
@RestController
@RequestMapping("/api/resources/image-texts")
public class ImageTextController {

    @Autowired
    private ImageTextService imageTextService;

    //创建图文资源
    @PostMapping
    public ResponseEntity<ImageText> create(@RequestBody ImageText imageText) {
        imageText.setCreatedAt(LocalDateTime.now());
        imageTextService.save(imageText);
        return ResponseEntity.status(201).body(imageText);
    }

    //获取所有图文资源
    @GetMapping
    public ResponseEntity<IPage<ImageText>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ImageText> pageable = new Page<>(page, size);
        QueryWrapper<ImageText> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_at");

        IPage<ImageText> result = imageTextService.page(pageable, queryWrapper);
        return ResponseEntity.ok(result);
    }

    //根据ID获取图文资源
    @GetMapping("/{id}")
    public ResponseEntity<ImageText> getById(@PathVariable Integer id) {
        ImageText imageText = imageTextService.getById(id);
        if (imageText == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(imageText);
    }

    //更新图文资源
    @PutMapping("/{id}")
    public ResponseEntity<ImageText> update(
            @PathVariable Integer id,
            @RequestBody ImageText updatedText) {

        ImageText existing = imageTextService.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setTitle(updatedText.getTitle());
        existing.setContent(updatedText.getContent());
        existing.setImagePath(updatedText.getImagePath());
        existing.setOwnerId(updatedText.getOwnerId());

        imageTextService.updateById(existing);
        return ResponseEntity.ok(existing);
    }

    //删除图文资源
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!imageTextService.removeById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
