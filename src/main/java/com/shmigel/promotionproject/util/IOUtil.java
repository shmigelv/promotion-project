package com.shmigel.promotionproject.util;

import com.shmigel.promotionproject.exception.GenericRestException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Log4j2
public class IOUtil {

    public static InputStream getInputStream(MultipartFile multipartFile) {
        try {
            return multipartFile.getInputStream();
        } catch (IOException e) {
            log.error("Couldn't get input stream from given file");
            throw new GenericRestException(HttpStatus.INTERNAL_SERVER_ERROR, "Couldn't get input stream from given file", e);
        }
    }

}
