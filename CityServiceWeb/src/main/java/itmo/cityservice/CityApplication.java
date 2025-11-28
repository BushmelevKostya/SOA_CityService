package itmo.cityservice;

import itmo.cityservice.controller.CityController;
import itmo.cityservice.cors.CorsFilter;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api/v1")
public class CityApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(CityController.class);
        classes.add(CorsFilter.class);
        return classes;
    }
}
