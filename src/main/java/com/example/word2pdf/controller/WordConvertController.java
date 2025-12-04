package com.example.word2pdf.controller;

import com.example.word2pdf.util.Word2PdfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/convert")
public class WordConvertController {

    private static final Logger log = LoggerFactory.getLogger(WordConvertController.class);

    /** Word → PDF */
    @PostMapping("/word2pdf")
    public ResponseEntity<byte[]> wordToPdf(@RequestParam("file") MultipartFile file) {
        log.info("收到 Word → PDF 请求, 文件名: {}", file.getOriginalFilename());

        try {
            ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();

            // 调用工具类
            Word2PdfUtil.convert(file.getInputStream(), pdfOut);

            byte[] pdfBytes = pdfOut.toByteArray();

            log.info("Word 转 PDF 成功, 输出大小 {} bytes", pdfBytes.length);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            log.error("Word 转 PDF 失败", e);
            throw new RuntimeException("Word 转 PDF 失败: " + e.getMessage(), e);
        }
    }

    /** Word → HTML */
    @PostMapping("/word2html")
    public ResponseEntity<byte[]> convertWordToHtml(@RequestParam("file") MultipartFile file) {
        log.info("收到 Word → HTML 请求, 文件名: {}", file.getOriginalFilename());

        try {
            com.aspose.words.Document doc =
                    new com.aspose.words.Document(file.getInputStream());

            ByteArrayOutputStream htmlStream = new ByteArrayOutputStream();

            com.aspose.words.HtmlSaveOptions saveOptions =
                    new com.aspose.words.HtmlSaveOptions();
            saveOptions.setExportImagesAsBase64(true);

            doc.save(htmlStream, saveOptions);

            byte[] htmlBytes = htmlStream.toByteArray();

            log.info("Word 转 HTML 成功, 输出大小 {} bytes", htmlBytes.length);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/html; charset=UTF-8"));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=result.html");

            return new ResponseEntity<>(htmlBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Word 转 HTML 失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("转换失败: " + e.getMessage()).getBytes());
        }
    }

}
