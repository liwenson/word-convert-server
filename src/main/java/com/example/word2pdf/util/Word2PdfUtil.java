package com.example.word2pdf.util;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;

import java.io.InputStream;
import java.io.OutputStream;

public class Word2PdfUtil {

    // 将 Word 转为 PDF，传入 InputStream/OutputStream（适合 Web）
    public static void convert(InputStream wordStream, OutputStream pdfStream) throws Exception {
        Document doc = new Document(wordStream);
        doc.save(pdfStream, SaveFormat.PDF);
    }

    // 文件路径版本
    public static void convert(String wordPath, String pdfPath) throws Exception {
        Document doc = new Document(wordPath);
        doc.save(pdfPath, SaveFormat.PDF);
    }
}
