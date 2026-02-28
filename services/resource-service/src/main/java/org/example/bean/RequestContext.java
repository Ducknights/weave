package org.example.bean;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Data
public class RequestContext {
    Long RequestId = 0L;
    Long UserId = 0L;
}
