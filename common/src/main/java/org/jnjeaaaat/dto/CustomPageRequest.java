package org.jnjeaaaat.dto;

public record CustomPageRequest(
        int page

) {

    public static final int PAGE_SIZE = 12;

}
