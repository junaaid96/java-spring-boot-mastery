package dev.junaaid96.mvc.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

// Replaces web.xml + dispatcher-servlet.xml from the old SpringMVC module: the container finds this class and boots Spring.
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    // Root context: services, repositories (shared across servlets).
    @Override
    protected Class<?>[] getRootConfigClasses() { return new Class<?>[] {AppConfig.class}; }

    // Web context: controllers, view resolvers (none extra here; WebConfig covers both).
    @Override
    protected Class<?>[] getServletConfigClasses() { return new Class<?>[] {WebConfig.class}; }

    // DispatcherServlet (front controller) handles every URL.
    @Override
    protected String[] getServletMappings() { return new String[] {"/"}; }
}
