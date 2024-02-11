package dev.thynanami.tenon

import io.minio.*
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.name

class TenonClient(endpoint: String, accessKey: String, secretKey: String, private val bucket: String) {
    private val minioClient: MinioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build()

    init {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build())
        }
    }

    fun upload(file: Path, name: String = file.name): Boolean { //TODO Logger
        return runCatching {
            minioClient.uploadObject(
                UploadObjectArgs.builder().bucket(bucket).`object`(name).filename(file.absolutePathString()).contentType(Files.probeContentType(file)).build()
            )
        }.isSuccess
    }

    fun upload(stream: InputStream,name:String,contentType: String,contentLength: Long):Boolean {
        return runCatching {
            minioClient.putObject(
                PutObjectArgs.builder().bucket(bucket).`object`(name).stream(stream,contentLength,-1).contentType(contentType).build()
            )
        }.isSuccess
    }

    fun delete(objectName:String):Boolean {
        return runCatching {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).`object`(objectName).build())
        }.isSuccess
    }

    fun serve(objectName: String,outputStream: OutputStream) {
        val data = minioClient.getObject(GetObjectArgs.builder().bucket(bucket).`object`(objectName).build())
        data.use { it.copyTo(outputStream) }
    }
}
