package org.jnjeaaaat.global.storage;

import org.jnjeaaaat.global.exception.CustomException;
import org.jnjeaaaat.global.exception.ErrorCode;

public class StorageException extends CustomException {

    public StorageException(ErrorCode errorCode) {
        super(errorCode);
    }

    public StorageException(ErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
