package com.longkd.chatgpt_openai.base.util

class Strings {
    companion object {
        const val EMPTY = ""
        const val EMPTY_ZERO = "00.00"
        const val ABOUT_BLANK = "about_blank"
        const val SLASH = "/"
        const val ROOT_PATH = "/*/"
        const val COPY_PATH = "_copy_path_"
        const val COPY_NAME = "_copy."
        const val DOT = "."
        const val VALID_FILE_NAME = "[|\\?*<\\\":>+\\[\\]\\/']+"
        const val UNKNOWN = "unknown"
        const val DOT_ZIP = ".zip"
        const val DEFAULT_TYPE = "*/*"
        const val DUMMY_FILE = ".ExFileManagerDummyFile.exfm"
        const val DUMMY_FILE_WRITE = ".ExFileManagerDummyWriteFile.exfm"
        const val EX_RECYCLE_BIN_FOLDER = ".exRecycleBinFolder"
        const val EX_RECYCLE_BIN_TAG = "EX_RECYCLE_BIN_TAG"
        val REGEX_GET_RENAME_NUMBER = "([0-9])".toRegex()
        const val PERCENT = "%"
        const val PER_C = "°C"
        const val COLOR_HTML_RED = "#FF4848"
        const val dat = "dat"
        const val bmp = "bmp"
        const val cache = "cache"
        const val css = "css"
        const val doc = "doc"
        const val docx = "docx"
        const val flac = "flac"
        const val gif = "gif"
        const val html = "html"
        const val jpeg = "jpeg"
        const val jpg = "jpg"
        const val js = "js"
        const val m4a = "m4a"
        const val mkv = "mkv"
        const val mp3 = "mp3"
        const val mp4 = "mp4"
        const val nomedia = "nomedia"
        const val ogg = "ogg"
        const val pdf = "pdf"

        const val txt = "txt"
        const val vcf = "Vcf Files"
        const val wav = "wav"
        const val webm = "webm"
        const val xlsx = "xlsx"
        const val xml = "xml"
        const val zip = "Zip Files"

        const val _3gp = "3gp"
        const val aac = "aac"
        const val amr = "amr"
        const val png = "png"
        const val emptyshow = "emptyshow"
        const val apk = "Apk Files"
    }
}

fun String.replaceLast(substring: String, replacement: String): String {
    val index = this.lastIndexOf(substring)
    return if (index == -1) this else this.substring(
        0,
        index
    ) + replacement + this.substring(index + substring.length)
}

fun String.findLastRenameNumber(): Int {
    return try {
        val xx = "([0-9])".toRegex().findAll(this)
        if (xx.count() > 0)
            xx.last().value.toInt()
        else 1
    } catch (e: Exception) {
        1
    }
}