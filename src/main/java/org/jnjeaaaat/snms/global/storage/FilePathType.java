package org.jnjeaaaat.snms.global.storage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FilePathType {

    MEMBER("member"),
    POST("post");

    private final String path;

    public String getPath(Long id) {
        return this.path + "/" + id;
    }

}
