package com.profITsoft.flightsystem.common.i18n;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

/**
 * LocaleChangeInterceptor is a custom interceptor to change the locale based on the Accept-Language header.
 */
@Component
@RequiredArgsConstructor
public class LocaleChangeInterceptor implements HandlerInterceptor {

    /**
     * The locale resolver to set the locale.
     */
    private final LocaleResolver localeResolver;

    /**
     * Pre-handle method to set the locale based on the Accept-Language header.
     *
     * @param request  the request
     * @param response the response
     * @param handler  the handler
     * @return true if the locale is set successfully
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String lang = request.getParameter("Accept-Language");
        if (lang != null) {
            Locale locale = Locale.forLanguageTag(lang);
            localeResolver.setLocale(request, response, locale);
        }
        return true;
    }
}
