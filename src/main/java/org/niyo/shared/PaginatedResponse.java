package org.niyo.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PaginatedResponse<E> {

    private List<E> content;

    private long page;

    private long size;

    private long numberOfElements;

    private long totalElements;

}
