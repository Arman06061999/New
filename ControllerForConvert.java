package com.rtk.rt.controller;

import com.rtk.rt.service.AudioVideoService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/1.0/convert")
@RequiredArgsConstructor
public class ControllerForConvert {


    private final AudioVideoService videoConvertM;

    @RequestMapping(path = "/audio", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<byte[]> convertToMp3(
                                               @RequestPart("file") MultipartFile file) throws IOException {

        return ResponseEntity.ok(videoConvertM.convertToMp3(file));

    }


    @RequestMapping(path = "/video", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<byte[]> convertToMp4(
                                               @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(videoConvertM.convertToMp4(file));
    }


    @RequestMapping(path = "/au_video", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<byte[]> extractAudio(
                                               @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(videoConvertM.extractAudio(file));

    }

    @RequestMapping(path = "/image", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<byte[]> convertToImage(
                                                 @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(videoConvertM.imageConvert(file));

    }
}





