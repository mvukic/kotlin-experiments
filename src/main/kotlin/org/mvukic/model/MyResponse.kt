package org.mvukic.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyResponse(

    @SerialName("v")
    val value: String
)
