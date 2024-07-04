package org.example.blog_project.config;

import jakarta.servlet.Filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${resource.path}")
    private String resourcePath;

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${content.resource.path}")
    private String contentResourcePath;

    @Value("${content.upload.path}")
    private String contentUploadPath;

    @Value("${main.resource.path}")
    private String mainResourcePath;

    @Value("${main.upload.path}")
    private String mainUploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(uploadPath)
                .addResourceLocations(resourcePath);

        registry.addResourceHandler(contentUploadPath)
                .addResourceLocations(contentResourcePath);

        registry.addResourceHandler(mainResourcePath)
                .addResourceLocations(mainUploadPath);
    }

//    @Bean
//    public FilterRegistrationBean logFilter(){
//        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
//        filterRegistrationBean.setFilter(new LoginCheckFilter());
//        filterRegistrationBean.setOrder(1);
//        filterRegistrationBean.addUrlPatterns("/*");
//        return filterRegistrationBean;
//    }
}
