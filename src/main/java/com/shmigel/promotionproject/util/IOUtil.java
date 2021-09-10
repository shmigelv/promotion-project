package com.shmigel.promotionproject.util;

import io.micrometer.core.instrument.util.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

public class IOUtil {

    public static String read(MultipartFile file) {
        try {
            return IOUtils.toString(file.getInputStream());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while loading file");
        }
    }

    public static String getMd5Checksum(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes());
    }

}
