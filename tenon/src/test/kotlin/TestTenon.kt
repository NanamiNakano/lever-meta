import dev.thynanami.tenon.TenonClient
import kotlin.io.path.Path

fun main() {
    val client = TenonClient(
        "https://a7c2d1910eea09496c20369fb12e66e2.eu.r2.cloudflarestorage.com",
        "ceff37e6cf5c1ff1a254efcf0eb46199",
        "a495bf420e0ce6575cbc0b8beb65653b4bfa72c6439c22d0bb8194fbf6b1250d",
        "test"
    )

    val file = Path("./test.json")

    client.upload(file)
}
