package org.jnjeaaaat.snms.global.storage;

import org.jnjeaaaat.snms.global.exception.CustomException;
import org.jnjeaaaat.snms.global.exception.ErrorCode;

public class StorageException extends CustomException {

    public StorageException(ErrorCode errorCode) {
        super(errorCode);
    }

    public StorageException(ErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
