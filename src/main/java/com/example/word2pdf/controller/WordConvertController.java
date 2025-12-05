package com.example.word2pdf.controller;

import com.example.word2pdf.util.Word2PdfUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/api/convert")
public class WordConvertController {

    // 临时存储预览文件(实际生产应使用 Redis 或文件系统)
    private static final ConcurrentHashMap<String, PreviewFile> previewCache = new ConcurrentHashMap<>();

    // 缓存过期时间(30分钟)
    private static final long CACHE_EXPIRY_MS = 30 * 60 * 1000;

    /** Word → PDF (下载) */
    @PostMapping("/word2pdf")
    @ResponseBody
    public ResponseEntity<byte[]> wordToPdf(@RequestParam("file") MultipartFile file) {
        try {
            ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
            Word2PdfUtil.convert(file.getInputStream(), pdfOut);
            byte[] pdfBytes = pdfOut.toByteArray();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=converted.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            throw new RuntimeException("Word 转 PDF 失败: " + e.getMessage(), e);
        }
    }

    /** Word → PDF (在线预览) */
    @PostMapping("/word2pdf/preview")
    @ResponseBody
    public ResponseEntity<?> wordToPdfPreview(@RequestParam("file") MultipartFile file) {
        try {
            ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
            Word2PdfUtil.convert(file.getInputStream(), pdfOut);
            byte[] pdfBytes = pdfOut.toByteArray();

            // 生成预览ID并缓存
            String previewId = UUID.randomUUID().toString();
            previewCache.put(previewId, new PreviewFile(pdfBytes, System.currentTimeMillis()));

            // 清理过期缓存
            cleanExpiredCache();

            // 返回预览页面URL
            return ResponseEntity.ok()
                    .body(new PreviewResponse(previewId, "/api/convert/preview/" + previewId));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("转换失败: " + e.getMessage()));
        }
    }

    /** 获取预览文件内容 */
    @GetMapping("/preview/{previewId}")
    @ResponseBody
    public ResponseEntity<byte[]> getPreviewFile(@PathVariable String previewId) {
        PreviewFile file = previewCache.get(previewId);

        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=preview.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file.content);
    }

    /** 预览页面 */
    @GetMapping("/preview-page/{previewId}")
    public String previewPage(@PathVariable String previewId, Model model) {
        if (!previewCache.containsKey(previewId)) {
            return "error";
        }
        model.addAttribute("previewId", previewId);
        return "preview";
    }

    /** Word → HTML (在线预览) */
    @PostMapping("/word2html/preview")
    @ResponseBody
    public ResponseEntity<?> wordToHtmlPreview(@RequestParam("file") MultipartFile file) {
        try {
            com.aspose.words.Document doc =
                    new com.aspose.words.Document(file.getInputStream());

            ByteArrayOutputStream htmlStream = new ByteArrayOutputStream();

            com.aspose.words.HtmlSaveOptions saveOptions =
                    new com.aspose.words.HtmlSaveOptions();
            saveOptions.setExportImagesAsBase64(true);

            doc.save(htmlStream, saveOptions);
            byte[] htmlBytes = htmlStream.toByteArray();

            // 生成预览ID并缓存
            String previewId = UUID.randomUUID().toString();
            previewCache.put(previewId, new PreviewFile(htmlBytes, System.currentTimeMillis()));

            cleanExpiredCache();

            return ResponseEntity.ok()
                    .body(new PreviewResponse(previewId, "/api/convert/preview-html/" + previewId));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("转换失败: " + e.getMessage()));
        }
    }

    /** 获取 HTML 预览内容 */
    @GetMapping("/preview-html/{previewId}")
    @ResponseBody
    public ResponseEntity<String> getHtmlPreview(@PathVariable String previewId) {
        PreviewFile file = previewCache.get(previewId);

        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(new String(file.content));
    }

    /** Word → HTML (下载) */
    @PostMapping("/word2html")
    @ResponseBody
    public ResponseEntity<byte[]> convertWordToHtml(@RequestParam("file") MultipartFile file) {
        try {
            com.aspose.words.Document doc =
                    new com.aspose.words.Document(file.getInputStream());

            ByteArrayOutputStream htmlStream = new ByteArrayOutputStream();

            com.aspose.words.HtmlSaveOptions saveOptions =
                    new com.aspose.words.HtmlSaveOptions();
            saveOptions.setExportImagesAsBase64(true);

            doc.save(htmlStream, saveOptions);
            byte[] htmlBytes = htmlStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/html; charset=UTF-8"));
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=result.html");

            return new ResponseEntity<>(htmlBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("转换失败: " + e.getMessage()).getBytes());
        }
    }

    /** 清理过期缓存 */
    private void cleanExpiredCache() {
        long now = System.currentTimeMillis();
        previewCache.entrySet().removeIf(entry ->
                now - entry.getValue().timestamp > CACHE_EXPIRY_MS
        );
    }

    /** 预览文件数据类 */
    private static class PreviewFile {
        byte[] content;
        long timestamp;

        PreviewFile(byte[] content, long timestamp) {
            this.content = content;
            this.timestamp = timestamp;
        }
    }

    /** 预览响应类 */
    private static class PreviewResponse {
        public String previewId;
        public String previewUrl;

        PreviewResponse(String previewId, String previewUrl) {
            this.previewId = previewId;
            this.previewUrl = previewUrl;
        }
    }

    /** 错误响应类 */
    private static class ErrorResponse {
        public String error;

        ErrorResponse(String error) {
            this.error = error;
        }
    }
}