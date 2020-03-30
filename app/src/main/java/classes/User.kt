package classes

data class User(var username: String,
                var email: String,
                var id: String) {
    constructor() : this("",  "", "")
}