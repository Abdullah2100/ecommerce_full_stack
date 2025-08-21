package com.example.hotel_mobile.Util

object Validation {

    fun emailValidation(email:String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }

    fun passwordCapitalValidation(password:String): Boolean {
        val passwordContainCharCapitaRegex = "^(?=(.*[A-Z].*[A-Z])).*$".toRegex()
        return password.matches(passwordContainCharCapitaRegex)
    }


    fun passwordSmallValidation(password:String): Boolean {
        val passwordContainCharSmallRegex = "^(?=(.*[a-z].*[a-z])).*$".toRegex()

        return password.matches(passwordContainCharSmallRegex)
    }
    fun passwordSpicialCharracterValidation(password:String): Boolean {
        val passwordContainSpicialCharecterRegex = """
                   ^(?=(.*[!@#\\${'$'}%^&*()_+|/?<>:'\\"-].*[!@#\\${'$'}%^&*()_+|/?<>:'\\"-])).*${'$'}
        """.trimIndent()
            .toRegex()

        return password.matches(passwordContainSpicialCharecterRegex)
    }

    fun passwordNumberValidation(password:String): Boolean {
        val passwordContainNumberRegex = "^(?=(.*\\d.*\\d)).*$".toRegex()

        return password.matches(passwordContainNumberRegex)
    }

    fun isValidMoney(number: String): Boolean {
        val regex = Regex("/^(?:100(?:\\.0(?:0)?)?|\\d{1,2}(?:\\.\\d{1,2})?)\$")
        return regex.containsMatchIn(number)
    }
}