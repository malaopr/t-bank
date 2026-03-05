import java.net.HttpURLConnection
import java.net.HttpsURLConnection
import java.net.URL
import java.util.Base64
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate

fun disableSslVerification() {
    val trustAll = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, trustAll, java.security.SecureRandom())
    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
    HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
}

fun sendRequest(url: String, method: String, body: String? = null, authHeader: String? = null): Pair<Int, String> {
    val connection = URL(url).openConnection() as HttpURLConnection
    connection.requestMethod = method

    if (body != null) {
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")
        connection.outputStream.use { os ->
            os.write(body.toByteArray())
            os.flush()
        }
    }

    if (authHeader != null) {
        connection.setRequestProperty("Authorization", authHeader)
    }

    val responseCode = connection.responseCode

    val responseBody = try {
        if (responseCode in 200..299) {
            connection.inputStream.bufferedReader().readText()
        } else {
            connection.errorStream?.bufferedReader()?.readText() ?: ""
        }
    } catch (e: Exception) {
        ""
    }

    connection.disconnect()
    return Pair(responseCode, responseBody)
}

fun main() {
    disableSslVerification()
    val encoder = Base64.getUrlEncoder().withoutPadding()
    val decoder = Base64.getUrlDecoder()

    // ========== TASK 1: HTTP-запросы ==========
    println("========== TASK 1: HTTP-запросы ==========\n")

    println("=== 1. GET /posts/1 ===")
    val (code1, body1) = sendRequest("https://jsonplaceholder.typicode.com/posts/1", "GET")
    println("Код: $code1")
    println("Тело: $body1\n")

    println("=== 2. POST /posts ===")
    val postBody = """{"title":"Test","body":"Test body","userId":1}"""
    val (code2, body2) = sendRequest("https://jsonplaceholder.typicode.com/posts", "POST", postBody)
    println("Код: $code2")
    println("Тело: $body2\n")

    println("=== 3. GET /posts/9999 (ошибка) ===")
    val (code3, body3) = sendRequest("https://jsonplaceholder.typicode.com/posts/9999", "GET")
    println("Код: $code3")
    if (code3 != 200) {
        println("Ошибка: пост не найден")
    }
    println()

    // ========== TASK 3: JWT авторизация ==========
    println("========== TASK 3: JWT авторизация ==========\n")

    println("=== 1. Сборка JWT ===")
    val header = """{"alg":"HS256","typ":"JWT"}"""
    val payload = """{"sub":"1","name":"Ivan Petrov","role":"student","iat":1234567890}"""
    val fakeSignature = "dummysignature"

    val encodedHeader = encoder.encodeToString(header.toByteArray())
    val encodedPayload = encoder.encodeToString(payload.toByteArray())
    val encodedSignature = encoder.encodeToString(fakeSignature.toByteArray())
    val token = "$encodedHeader.$encodedPayload.$encodedSignature"

    println("Токен: $token\n")

    println("=== 2. Декодирование JWT ===")
    val parts = token.split(".")
    val decodedHeader = String(decoder.decode(parts[0]))
    val decodedPayload = String(decoder.decode(parts[1]))

    println("Header: $decodedHeader")
    println("Payload: $decodedPayload\n")

    println("=== 3. GET /bearer (с токеном) ===")
    val (code4, body4) = sendRequest("https://httpbin.org/bearer", "GET", authHeader = "Bearer $token")
    println("Код: $code4")
    println("Тело: $body4\n")

    println("=== 4. GET /bearer (без токена) ===")
    val (code5, body5) = sendRequest("https://httpbin.org/bearer", "GET")
    println("Код: $code5")
    println("Ожидается 401\n")

    println("=== 5. Подмена payload ===")
    val fakePayload = """{"sub":"1","name":"Ivan Petrov","role":"admin","iat":1234567890}"""
    val fakeEncodedPayload = encoder.encodeToString(fakePayload.toByteArray())
    val fakeToken = "$encodedHeader.$fakeEncodedPayload.$encodedSignature"

    println("Новый токен (role=admin): $fakeToken")
    val (code6, body6) = sendRequest("https://httpbin.org/bearer", "GET", authHeader = "Bearer $fakeToken")
    println("Код: $code6")
    println("Сервер отвергнет этот токен, потому что signature не совпадает.")
    println("Даже если подменить payload, хеш-сигнатура будет неверной.")
}
