package com.github.aakumykov.sync_dir_to_cloud.view.other.utils

import android.content.Context
import android.content.res.Resources

// TODO: тест
class TextMessage {

    constructor(text: String) {
        this.text = text
        this.formatArguments = arrayOf()
    }

    constructor(textId: Int) {
        this.textId = textId
        this.formatArguments = arrayOf()
    }

    constructor(textId: Int, vararg formatArguments: Any) {
        this.textId = textId
        this.formatArguments = formatArguments
    }


    private var text: String? = null
    private var textId: Int? = null
    private var formatArguments: Array<out Any>


    fun get(context: Context): String {
        return get(context.resources)
    }


    fun get(resources: Resources): String {
        if (formatArguments.size > 0 && null != textId)
            return formattedText(resources)
        else if (null != textId)
            return textFromResource(resources)
        else if (null != text)
            return text!!
        else
            throw IllegalStateException("Bad object configuration:\n text: $text\n textId: $textId\n formatArguments: $formatArguments\n")
    }


    private fun formattedText(context: Context): String {
        return formattedText(context.resources)
    }

    private fun formattedText(resources: Resources): String {
        return resources.getString(textId!!, *formatArguments)
    }


    private fun textFromResource(context: Context): String {
        return textFromResource(context.resources)
    }

    private fun textFromResource(resources: Resources): String {
        return resources.getString(textId!!)
    }
}
