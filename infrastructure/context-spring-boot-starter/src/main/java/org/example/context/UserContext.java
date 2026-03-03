package org.example.context;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserContext implements Serializable {
    private Long userId;
    private List<String> roles;
    private List<String> permissions;
    private Map<String, Object> extraInfo;

    public void clear() {
        userId = null;
        roles = null;
        permissions = null;
        extraInfo = null;
    }
}
