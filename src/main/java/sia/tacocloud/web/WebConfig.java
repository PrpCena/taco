package sia.tacocloud.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private IngredientByIdConverter ingredientByIdConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(ingredientByIdConverter);
    }
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/abc").setViewName("home");
        registry.addViewController("/login");
    }

}