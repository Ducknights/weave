package org.example.auto;

import org.example.filter.UserContextFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@ConditionalOnWebApplication
public class ContextAutoConfiguration {

    @Bean
    public FilterRegistrationBean<UserContextFilter> userContextFilterRegistration(UserContextFilter userContextFilter) {
        FilterRegistrationBean<UserContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(userContextFilter);
        registration.addUrlPatterns("/admin");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registration;
    }
}
