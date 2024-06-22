package com.profITsoft.flightsystem.common.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Utility class to generate pagination headers.
 */
@Service
public class PaginationHeadersUtils {

    /**
     * Create the pagination headers for the given page and pageable.
     *
     * @param page     The page object containing the data.
     * @param pageable The pageable object containing the pagination details.
     * @return The HttpHeaders object containing the pagination headers.
     */
    public HttpHeaders createPaginationHeaders(Page<?> page, Pageable pageable) {
        HttpHeaders headers = new HttpHeaders();

        if (page.hasPrevious()) {
            headers.add("Link", generatePageLinkHeader(pageable.previousOrFirst(), "prev"));
        }

        if (page.hasNext()) {
            headers.add("Link", generatePageLinkHeader(pageable.next(), "next"));
        }

        headers.add("Link", generatePageLinkHeader(Pageable.ofSize(pageable.getPageSize()), "first"));

        if (page.getTotalPages() > 0)
            headers.add("Link", generatePageLinkHeader(pageable.withPage(page.getTotalPages() - 1), "last"));

        return headers;
    }

    /**
     * Generate the page link header for the given pageable and relation.
     *
     * @param pageable The pageable object containing the pagination details.
     * @param rel      The relation of the page link.
     * @return The page link header.
     */
    private String generatePageLinkHeader(Pageable pageable, String rel) {
        String url = ServletUriComponentsBuilder.fromCurrentRequest()
                .replaceQueryParam("page", pageable.getPageNumber())
                .toUriString();
        return String.format("<%s>; rel=\"%s\"", url, rel);
    }
}
