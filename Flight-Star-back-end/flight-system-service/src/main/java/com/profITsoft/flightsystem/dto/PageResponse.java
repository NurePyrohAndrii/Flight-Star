package com.profITsoft.flightsystem.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Page response class to represent a page of data
 *
 * @param <T> type of the content
 */
@Getter
@Builder
@Jacksonized
public class PageResponse<T> {

    T content;

    int page;

    int size;

    int totalPages;

    long totalElements;

    /**
     * Create a page response from a page
     *
     * @param page page
     * @param <T> type of the content
     * @return page response
     */
    public static <T> PageResponse<List<T>> of(Page<T> page) {
        return PageResponse.<List<T>>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }
}
