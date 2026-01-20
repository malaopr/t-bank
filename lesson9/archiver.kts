import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.*

fun archive(source: String, target: String, vararg exts: String) {
    val dir = File(source)
    if (!dir.exists()) {
        println("Папка $source не найдена")
        return
    }

    FileOutputStream(target).use { fos ->
        ZipOutputStream(fos).use { zos ->
            dir.walk().filter { it.isFile }.forEach { file ->
                val needAdd = exts.isEmpty() || exts.any { file.name.endsWith(it, true) }

                if (needAdd) {
                    val path = dir.toURI().relativize(file.toURI()).path
                    zos.putNextEntry(ZipEntry(path))
                    FileInputStream(file).use { it.copyTo(zos) }
                    zos.closeEntry()
                    println("$path (${file.length()} байт)")
                }
            }
        }
    }
    println("Архив $target создан")
}

archive("logs", "logs.zip", ".logs")
archive("logs/config", "config.zip")

