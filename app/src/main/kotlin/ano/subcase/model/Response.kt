package ano.subcase.model

data  class Response(
    var statusCode: Int = 200,
    var headers: Map<String, String> = emptyMap(),
    var body: String = "",
) {
    override fun toString(): String {
        return "Response(statusCode=$statusCode, headers=$headers, body=$body)"
    }
}