package ano.subcase.model

//https://nsloon.app/LoonManual/#/cn/script?id=%e9%85%8d%e7%bd%ae%e8%af%ad%e6%b3%95-1
data class Request(
    val url: String,
    val method: String,
    val headers: Map<String, String>,
    val body: String
)