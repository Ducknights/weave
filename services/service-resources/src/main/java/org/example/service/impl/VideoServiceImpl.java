package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.Video;
import org.example.mapper.VideoMapper;
import org.example.service.VideoService;
import org.springframework.stereotype.Service;

@Service
public class VideoServiceImpl
        extends ServiceImpl<VideoMapper, Video>
        implements VideoService {
}
