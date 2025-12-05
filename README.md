# word文件网页预览

word文件转换pdf文件和html文件

## API 端点

|端点|方法| 功能      |
|------|------|---------|
|/api/convert/word2pdf|POST|Word → PDF 下载|
|/api/convert/word2html|POST|Word → HTML 下载|

## 分支

### main 分支转换pdf文件和html文件

### filePreview 支持页面文件预览

启动后访问页面  http://localhost:8080/

|端点|方法| 功能      |
|------|------|---------|
|/|GET| 主页(上传页面) |
|/api/convert/word2pdf|POST|Word → PDF 下载|
|/api/convert/word2pdf/preview|POST|Word → PDF 预览(返回预览ID)|
|/api/convert/word2html|POST|Word → HTML 下载|
|/api/convert/word2html/preview|POST|Word → HTML 预览(返回预览ID)|
|/api/convert/preview/{id}|GET|获取 PDF 文件内容|
|/api/convert/preview-page/{id}|GET|PDF 预览页面|
|/api/convert/preview-html/{id}|GET|HTML 预览内容|






