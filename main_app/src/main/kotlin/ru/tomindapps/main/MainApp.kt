package ru.tomindapps.main

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import java.util.regex.Pattern

class MainApp {
    fun start() {
        val bot = bot {
            token = "5821512388:AAGtX3TNAwu7QuN_pc9hx915IgILAW8soVc"
            dispatch {
                text {
                    text
                        .checkPhone {
                            (ERROR + it).send(bot, message.chat.id)
                        }
                        .clean()
                        .addToUrl()
                        .send(bot, message.chat.id)
                }
            }
        }
        bot.startPolling()
    }

    private fun String.send(bot: Bot, id: Long) {
        if (this.isNotEmpty()) bot.sendMessage(ChatId.fromId(id), text = this)
    }

    private fun String.checkPhone(onFailure: (String) -> Unit): String {
        return this.phoneValidator {
            onFailure(this)
            Strings.Empty
        }
    }

    private fun String.clean(): String {
        return if (this.isNotEmpty()) {
            removePrefix("+")
        } else {
            this
        }
    }

    private fun String.addToUrl(): String {
        return if (this.isNotEmpty()) {
            "https://api.whatsapp.com/send/?phone=$this"//&text&type=phone_number
        } else {
            this
        }
    }

    private companion object {
        private const val ERROR = "Unable to determine the correct phone number - "
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
