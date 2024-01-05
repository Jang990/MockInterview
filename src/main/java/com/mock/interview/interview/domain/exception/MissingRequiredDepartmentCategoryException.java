package com.mock.interview.interview.domain.exception;

import com.mock.interview.global.exception.CustomClientException;
import org.springframework.http.HttpStatus;

/**
 * 필드 카테고리를 만들 때는 분야가 필수임
 * 예를 들어 'IT'분야 하위에 '백엔드'필드가 있는 형식으로 고정함
 */
public class MissingRequiredDepartmentCategoryException extends CustomClientException {
    public MissingRequiredDepartmentCategoryException() {
        super(HttpStatus.BAD_REQUEST, "required.jobcategory.field");
    }

    public MissingRequiredDepartmentCategoryException(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST, "required.jobcategory.field");
    }
}
