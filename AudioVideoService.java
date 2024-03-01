package com.rtk.rt.service;

import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;

import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_MP3;

//Написать три метода
//передаешь картинку, в параметрах во что мы хотим ее преобразовать, парметры сжатия
//Для видео и аудио то же
@Service
@Primary
@Slf4j
@AllArgsConstructor
public class AudioVideoService {

    private static final String MP3 = "mp3";
    private static final String WAV = ".wav";
    private static final String AAC = ".aac";
    private static final String OGG = ".ogg";
    private static final String WMA = ".wma";
    private static final String WAV_FORMAT = "wav";
    private static final String AAC_FORMAT = "aac";
    private static final String OGG_FORMAT = "ogg";
    private static final String WMA_FORMAT = "wma";


    private static final String MP4 = "mp4";
    public static final String AVI = ".avi";
    public static final String WMV = ".wmv";
    private static final String MOV = ".mov";
    private static final String AVI_FORMAT = ".avi";
    private static final String WMV_FORMAT = "wmv";
    private static final String MOV_FORMAT = "mov";

    private static final String PNG = "PNG";
    private static final String BMP_FORMAT = ".bmp";
    private static final String GIF_FORMAT = ".gif";
    private static final String TIFF_FORMAT = ".tiff";
    private static final String RAW_FORMAT = ".raw";
    private static final String PNG_FORMAT = ".png";
    private static final String RAW = "raw";
    private static final String GIF = "gif";
    private static final String BMP = "bmp";
    private static final String TIFF = "tiff";

    // 1.
    public File getFile(MultipartFile file) throws IOException {
        if (file == null) return null;

        String originalFileName = file.getOriginalFilename();
        File f = createFile(originalFileName);
        file.transferTo(f.getAbsoluteFile());

        return f;
    }

    public File createFile(String name) {
        return new File(name);
    }

    public byte[] convertToMp3(MultipartFile multipartFile) throws IOException {
        Frame audioSamples = null;
        FFmpegFrameRecorder recorder = null;
        File file = getFile(multipartFile);
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file)) {
            try {
                grabber.start();
            } catch (FFmpegFrameGrabber.Exception e) {
                throw new RuntimeException(e);
            }
            recorder = new FFmpegFrameRecorder(file, grabber.getAudioChannels());
            String sourceFormat = null;
            if (file.getName().endsWith(WAV)) {
                sourceFormat = WAV_FORMAT;
            } else if (file.getName().endsWith(AAC)) {
                sourceFormat = AAC_FORMAT;
            } else if (file.getName().endsWith(OGG)) {
                sourceFormat = OGG_FORMAT;
            } else if (file.getName().endsWith(WMA)) {
                sourceFormat = WMA_FORMAT;
            }
            if (sourceFormat != null) {
                recorder.start();
                recorder.setFormat(MP3);
                recorder.setAudioCodec(AV_CODEC_ID_MP3);
                recorder.setAudioBitrate(128000);
            }
            recorder.setAudioChannels(2);
            recorder.setSampleRate(44100);

            recorder.setAudioQuality(0);
            recorder.setAudioOption("aq", "10");

            try {
                while ((audioSamples = grabber.grab()) != null) {
                    recorder.setTimestamp(grabber.getTimestamp());
                    recorder.record(audioSamples);
                }

            } catch (Exception e) {
                log.error(e.getMessage());
            }
            grabber.stop();
        } catch (FrameGrabber.Exception | FFmpegFrameRecorder.Exception e) {
            throw new RuntimeException(e);
        }
        try {
            recorder.stop();
        } catch (FFmpegFrameRecorder.Exception e) {
            throw new RuntimeException(e);
        }
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new byte[0];
    }


    public byte[] convertToMp4(MultipartFile multipartFile) throws IOException {
        Frame audioSamples;
        File file = getFile(multipartFile);
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file)) {
            grabber.start();

            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(file, grabber.getAudioChannels())) {
                recorder.setAudioOption("crf", "0");// опция, которая позволяет вам настроить качество аудио в вашем файле MP3.

                String sourceFormat = null;
                if (file.getName().endsWith(AVI)) {
                    sourceFormat = AVI_FORMAT;
                } else if (file.getName().endsWith(MOV)) {
                    sourceFormat = MOV_FORMAT;
                } else if (file.getName().endsWith(WMV)) {
                    sourceFormat = WMV_FORMAT;
                }
                if (sourceFormat != null) {
                    recorder.start();
                    recorder.setFormat(MP4);
                    recorder.setVideoCodec(0);
                    recorder.setAudioCodec(AV_CODEC_ID_MP3);
                    recorder.setAudioBitrate(128000);
                    recorder.setVideoBitrate(0);
                    recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
                    recorder.setAudioChannels(2);
                    recorder.setSampleRate(0);
                    recorder.setAudioQuality(0);
                    recorder.setAudioOption("aq", "10");
                }
                //  recorder.setVideoCodec(0); // Установка кодека для видео (0 - H.264)
//                recorder.setAudioCodec(AV_CODEC_ID_MP3);
//                recorder.setAudioBitrate(128000);
//                recorder.setVideoBitrate(0);
//                recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
//                recorder.setAudioChannels(audioChannels);
//                recorder.setSampleRate(0);
//                recorder.setAudioQuality(0);
//                recorder.setAudioOption("aq", "10");

                try {
                    while ((audioSamples = grabber.grab()) != null) {
                        recorder.setTimestamp(grabber.getTimestamp());
                        recorder.record(audioSamples);
                    }

                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            } catch (FrameRecorder.Exception e) {
                throw new RuntimeException(e);
            }
            grabber.stop();
        } catch (FrameGrabber.Exception e) {
            log.error(e.getMessage());
        }
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new byte[0];
    }

    public byte[] extractAudio(MultipartFile multipartFile) throws IOException {
        File file = getFile(multipartFile);
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file)) {
            try {
                grabber.start();

                try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(file.getAbsolutePath() + ".audio." + MP3, grabber.getAudioChannels())) {
                    recorder.start();
                    recorder.setAudioCodec(grabber.getAudioCodec());
                    Frame frame;
                    while ((frame = grabber.grabFrame()) != null) {
                        recorder.record(frame);
                    }
                    grabber.stop();
                    recorder.stop();
                    grabber.release();
                    recorder.release();
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new byte[0];
    }


    public byte[] imageConvert(MultipartFile multipartFile) throws IOException {
        File file = getFile(multipartFile);
        String sourceFormat = null;
        try {

            if (file.getName().endsWith(RAW_FORMAT)) {
                sourceFormat = RAW;
            } else if (file.getName().endsWith(GIF_FORMAT)) {
                sourceFormat = GIF;
            } else if (file.getName().endsWith(TIFF_FORMAT)) {
                sourceFormat = TIFF;
            } else if (file.getName().endsWith(BMP_FORMAT)) {
                sourceFormat = BMP;
            }
            if (sourceFormat != null) {
//                 BufferedImage image1 = ImageIO.read(new File(sourceFormat));
//                 ImageIO.write(image1, PNG, responseFile);
                BufferedImage image1 = ImageIO.read(file);
                ImageWriter writer = ImageIO.getImageWritersByFormatName(PNG).next();
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                FileImageOutputStream output = new FileImageOutputStream(file);
                writer.setOutput(output);
                writer.write(null, new javax.imageio.IIOImage(image1, null, null), param);
                writer.dispose();
                output.close();
            }
            log.info("Successfully convert");
        } catch (IOException e) {
            log.error("Error converting image: " + e.getMessage());
        }
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new byte[0];
    }


}


