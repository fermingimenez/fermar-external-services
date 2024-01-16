package py.com.fermar.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { 
		"py.com.fermar.commons.config",
		
		"py.com.fermar.external.config",
		"py.com.fermar.external.service",
		"py.com.fermar.external.utils",
		"py.com.fermar.external.controllers",

		"org.springframework.web.client"}, includeFilters = {
		@ComponentScan.Filter(value = RestTemplate.class, type = FilterType.ASSIGNABLE_TYPE)}
)
public class WebMvcConfig implements WebMvcConfigurer {

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

}