package ru.tomindapps.main

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.User
import kotlinx.datetime.Clock
import java.util.regex.Pattern

class MainApp {

    private val botToken = System.getenv("WDT")

    fun start() {
        System.getenv().forEach {
            println("${it.key} : ${it.value}")
        }

        println("Starting bot with token - $botToken")
        val bot = bot {
            token = botToken
            dispatch {
                command("start") {
                    bot.send(message.from.getGreetingMessage(), update.message!!.chat.id)
                }
                text {
                    val result = text
                        .log(message.from?.username.orEmpty(), message.from?.id)
                        .clearify()
                        .checkPhone {
                            bot.send(message.from.getErrorMessage(it), message.chat.id)
                        }
                        .clean()
                        .addToUrl()

                    if (result.isNotEmpty()) {
                        bot.send(message.from.getFriendMessage(), message.chat.id)
                        bot.send(result, message.chat.id)
                    }
                }
            }
        }
        bot.startPolling()
        println("Bot started")
    }

    private fun User?.getGreetingMessage(): String {
        return if (this?.languageCode?.contains("ru", true) == true) {
            GREETING_RU
        } else {
            GREETING_EN
        }.trimIndent()
    }

    private fun User?.getErrorMessage(text: String): String {
        return if (this?.languageCode?.contains("ru", true) == true) {
            if (text.isNotEmpty()) {
                ERROR_RU_1 + text + ERROR_RU_2
            } else {
                ERROR_RU_3
            }
        } else {
            if (text.isNotEmpty()) {
                ERROR_EN_1 + text + ERROR_EN_2
            } else {
                ERROR_EN_3
            }
        }.trimIndent()
    }

    private fun User?.getFriendMessage(): String {
        return if (this?.languageCode?.contains("ru", true) == true) {
            FRIEND_RU
        } else {
            FRIEND_EN
        }.trimIndent()
    }

    private fun String.log(name: String, id: Long?): String {
        val time = Clock.System.now().toString()
        if (this.isNotEmpty()) println("$time: message $this from $name with id $id")
        return this
    }

    private fun String.clearify(): String {
        val reg = "^(\\+)|[^\\d\\n]".toRegex()
        return this.replace(reg, "")
    }

    private fun Bot.send(text: String, id: Long) {
        if (text.isNotEmpty()) this.sendMessage(ChatId.fromId(id), text = text, parseMode = ParseMode.MARKDOWN)
    }

    private fun String.checkPhone(onFailure: (String) -> Unit): String {
        if (this.contains("start")) return Strings.Empty
        return this.phoneValidator {
            onFailure(this)
            Strings.Empty
        }
    }

    private fun String.clean(): String {
        var result = if (this.isNotEmpty()) {
            this.removePrefix("+")
        } else {
            this
        }

        if (result.length == 11 && result.startsWith('8')) {
            result = result.replaceFirst('8', '7')
        }
        return result
    }

    private fun String.addToUrl(): String {
        return if (this.isNotEmpty()) {
            "https://api.whatsapp.com/send/?phone=$this"//&text&type=phone_number
        } else {
            this
        }
    }

    private companion object {
        private const val FRIEND_RU = "Вот твоя прямая ссылка, друг!"
        private const val FRIEND_EN = "Here's a direct link, friend!"
        private const val ERROR_RU_1 = "Не удалось правильно определить номер телефона - \"*"
        private const val ERROR_RU_2 = "*\", попробуй ещё раз:)"
        private const val ERROR_RU_3 = "Не удалось правильно определить номер телефона, попробуй ещё раз:)"
        private const val ERROR_EN_1 = "Unable to determine the correct phone number - \"*"
        private const val ERROR_EN_2 = "*\", try it again please:) "
        private const val ERROR_EN_3 = "Unable to determine the correct phone number, try it again please:)"
        private const val GREETING_RU = """
            Привет! Я чат-бот и помогу тебе открыть чат WhatsApp не сохраня контакт в книгу контактов! 
            Просто отправь мне номер в международном формате или начиная с "8"!
            Например +79991114455 или 79991114455 или 89991114455!
            К сожалению я понимаю только цифры, поэтому могу ответить тебе ошибкой!:)
        """
        private const val GREETING_EN = """
            Hi! I'm a chatbot and will help you open a WhatsApp chat without keeping a contact in your contact book! 
            Just send me a number in international format or starting with "8"!
            For example +79991114455 or 79991114455 or 89991114455!
            Unfortunately, I only understand numbers, so I can answer you by mistake:)
        """
    }

}


object PhoneValidator {
    private const val REG = "^\\+?[1-9][0-9]{7,14}$"
    private var PATTERN: Pattern = Pattern.compile(REG)
    fun isPhoneNumber(input: String): Boolean = PATTERN.matcher(input).find()
}

object Strings {
    const val Empty = ""
}


fun String.phoneValidator(onFailure: (String) -> String): String =
    if (PhoneValidator.isPhoneNumber(this)) this else onFailure(this)
