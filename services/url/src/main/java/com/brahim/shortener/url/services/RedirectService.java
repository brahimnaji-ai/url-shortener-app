package com.brahim.shortener.url.services;

import com.brahim.shortener.url.exception.BusinessException;
import com.brahim.shortener.url.URL;
import com.brahim.shortener.url.URLRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.brahim.shortener.url.exception.ErrorCode.URL_EXPIRED;
import static com.brahim.shortener.url.exception.ErrorCode.URL_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RedirectService {
    private final URLRepository repository;
    public String getOriginalURL(String code) {
        URL url = repository.findByShortCode(code)
                .orElseThrow(()-> new BusinessException(URL_NOT_FOUND));
        if(!url.isActive()){
            throw new BusinessException(URL_EXPIRED);
        }
        return url.getOriginalURL();
    }
}
