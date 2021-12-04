package com.romanm.jwtservicedata.controllers;

import com.romanm.jwtservicedata.constants.Api;
import com.romanm.jwtservicedata.services.interfaces.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = Api.API_PREFIX)
public class FileUploadController {
    @Autowired
    private StorageService storageService;

    /**
     * Получить файл изображения по ссылке
     * @param userId String
     * @return  Mono<ResponseEntity<byte[]>>
     */
    @GetMapping(value = Api.API_USER_IMAGE)
    public Mono<ResponseEntity<byte[]>> getFile(
            @RequestParam(value = Api.PARAM_USER_ID, defaultValue = "", required = true) String userId,
            @RequestParam(value = Api.PARAM_FILE_ID, defaultValue = "", required = true) String fileName) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.IMAGE_JPEG_VALUE);

        return Mono.create(sink -> {
            sink.success(new ResponseEntity<>(this.storageService.getFile(userId, fileName), headers, HttpStatus.OK));
        });
    }

    /**
     * Сохранить/изменить файл
     * @param userId String
     * @param file Mono<FilePart>
     * @return Mono<ResponseEntity<?>>
     */
    @PostMapping(value = Api.API_USER_IMAGES, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<?>> saveFile(
            @RequestPart(value = Api.PARAM_USER_ID, required = true) String userId,
            @RequestPart(value = Api.PARAM_FILE, required = true) Mono<FilePart> file) {

        return Mono.create(sink -> {
            this.storageService.save(userId, file).doOnSuccess(res -> {
                sink.success(ResponseEntity.ok(res));
            }).subscribe();
        });
    }

    /**
     * Удалить выбранный файл
     * @param userId String
     * @param fileName String
     * @return Mono<ResponseEntity<?>>
     */
    @DeleteMapping(value = Api.API_USER_IMAGES, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<?>> deleteFile(
            @RequestPart(value = Api.PARAM_USER_ID, required = true) String userId,
            @RequestPart(value = Api.PARAM_FILE_ID, required = true) String fileName) {

        return Mono.create(sink -> {
            this.storageService.remove(userId, fileName).doOnSuccess(res -> {
                sink.success(res ? ResponseEntity.accepted().build(): ResponseEntity.status(HttpStatus.NOT_MODIFIED).build());
            }).subscribe();
        });
    }

    /**
     * Удалить все файлы (изображения) каталога пользователя
     * @param userId String
     * @return Mono<ResponseEntity<?>>
     */
    @DeleteMapping(value = Api.API_USER_IMAGES_ALL, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<?>> deleteFiles(@RequestPart(value = Api.PARAM_USER_ID, required = true) String userId) {
        return Mono.create(sink -> {
            this.storageService.removeAll(userId).doOnSuccess(res -> {
                sink.success(res ? ResponseEntity.accepted().build(): ResponseEntity.status(HttpStatus.NOT_MODIFIED).build());
            }).subscribe();
        });
    }


    @PostMapping(value = Api.API_USER_IMAGES_MULTI)
    public Mono<ResponseEntity<?>> saveFluxFiles(
            @RequestPart(value = Api.PARAM_USER_ID, required = true) String userId,
            @RequestPart(value = Api.PARAM_FILES, required = true) Flux<FilePart> files) {

        return Mono.create(sink -> {
            this.storageService.saveAll(userId, files)
                    .collectList()
                    .doOnSuccess(res -> {
                        sink.success(ResponseEntity.ok(res));
                    }).subscribe();
        });
    }
}
