package com.bianjf.exceptions;

import com.bianjf.enums.ExceptionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomDefaultException extends RuntimeException {
    private static final long serialVersionUID = -713754935165930288L;

    private ExceptionEnum exceptionEnum;

    public CustomDefaultException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomDefaultException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getDesc());
        this.exceptionEnum = exceptionEnum;
    }

    public CustomDefaultException(String message) {
        super(message);
    }

    public CustomDefaultException(ExceptionEnum exceptionEnum, Throwable cause) {
        super(exceptionEnum.getDesc(), cause);
        this.exceptionEnum = exceptionEnum;
    }
}
