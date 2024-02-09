package dev.thynanami.plugins

import dev.thynanami.tenon.TenonClient
import dev.thynanami.utils.appConfig

val tenonClient = TenonClient(
    appConfig.s3Config.endpoint,
    appConfig.s3Config.accessKey,
    appConfig.s3Config.secretKey,
    appConfig.s3Config.bucket
)
