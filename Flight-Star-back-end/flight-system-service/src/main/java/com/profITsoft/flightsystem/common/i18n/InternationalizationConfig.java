package com.profITsoft.flightsystem.common.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

/**
 * InternationalizationConfig class is a configuration class that configures the locale resolver and message source beans.
 */
@Configuration
public class InternationalizationConfig implements WebMvcConfigurer {

    /**
     * This method creates a message source bean that reads messages from the i18n/messages.properties file.
     * @return ReloadableResourceBundleMessageSource
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * This method creates a locale resolver bean that resolves the locale based on the Accept-Language header.
     * @return AcceptHeaderLocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(new Locale("uk", "UA"));
        return localeResolver;
    }

    /**
     * This method adds an interceptor that changes the locale based on the locale parameter in the request.
     * @param registry InterceptorRegistry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleChangeInterceptor(localeResolver()));
    }
}
