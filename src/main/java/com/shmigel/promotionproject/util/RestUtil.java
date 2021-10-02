package com.shmigel.promotionproject.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class RestUtil {

    public static URI createUriForResourceId(String path, Long id) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(path + "/{id}")
                .buildAndExpand(id)
                .toUri();
    }

}
