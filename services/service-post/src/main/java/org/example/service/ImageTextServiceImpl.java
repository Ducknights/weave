package org.example.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.ImageText;
import org.example.mapper.ImageTextMapper;
import org.springframework.stereotype.Service;

@Service
public class ImageTextServiceImpl
        extends ServiceImpl<ImageTextMapper, ImageText>
        implements ImageTextService {
}
