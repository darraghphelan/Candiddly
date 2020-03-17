package Classes

data class IDList(
    val friends: List<String>? = null,
    val received: List<String>? = null,
    val sent: List<String>? = null,
    val group: MutableList<String>? = null,
    val images: List<String>? = null
)