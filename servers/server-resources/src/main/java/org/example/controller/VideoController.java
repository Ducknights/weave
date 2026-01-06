//package org.example.controller;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//
//@CrossOrigin
//@RestController
//@RequestMapping("/api/resources/videos")
//public class VideoController {
//
//    @Autowired
//    private VideoService videoService;
//
//    @PostMapping
//    public ResponseEntity<Video> create(@RequestBody Video video) {
//        video.setCreatedAt(LocalDateTime.now());
//        videoService.save(video);
//        return ResponseEntity.status(201).body(video);
//    }
//
//    @GetMapping
//    public ResponseEntity<IPage<Video>> getAll(
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "10") int size) {
//
//        Page<Video> pageable = new Page<>(page, size);
//        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
//        queryWrapper.orderByDesc("created_at");
//
//        IPage<Video> result = videoService.page(pageable, queryWrapper);
//        return ResponseEntity.ok(result);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Video> getById(@PathVariable Integer id) {
//        Video video = videoService.getById(id);
//        if (video == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(video);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Video> update(
//            @PathVariable Integer id,
//            @RequestBody Video updatedVideo) {
//
//        Video existing = videoService.getById(id);
//        if (existing == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        existing.setTitle(updatedVideo.getTitle());
//        existing.setContent(updatedVideo.getContent());
//        existing.setVideoPath(updatedVideo.getVideoPath());
//        existing.setOwnerId(updatedVideo.getOwnerId());
//
//        videoService.updateById(existing);
//        return ResponseEntity.ok(existing);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Integer id) {
//        if (!videoService.removeById(id)) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.noContent().build();
//    }
//}