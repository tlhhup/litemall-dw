package org.tlh.profile.exception;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-03-30
 */
public class OozieTaskException extends RuntimeException {

    public OozieTaskException() {
    }

    public OozieTaskException(String message) {
        super(message);
    }

    public OozieTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
