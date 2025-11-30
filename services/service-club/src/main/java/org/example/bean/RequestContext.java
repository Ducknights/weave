package org.example.bean;


import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@Data
@RequestScope
public class RequestContext {
    private String requestId;
    private String userId;
}
