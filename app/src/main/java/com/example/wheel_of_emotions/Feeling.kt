package com.example.wheel_of_emotions

import android.content.Context
import android.graphics.Color
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

class Feeling {
    var id: Int = 0
        private set
    var uniqueName: String = ""
        private set
    var nameLt: String = ""
        private set
    var nameEn: String = ""
        private set
    var colorARGB: Int = 0
        private set
    var colorDec: Int = 0
        private set
    private var position: String = ""
    private var originCenterLt: String? = ""
    private var originInnerLt: String? = ""
    private var originCenterEn: String? = ""
    private var originInnerEn: String? = ""
    private var colorHex: String = ""
    private var colorR: Int = 0
    private var colorG: Int = 0
    private var colorB: Int = 0

    companion object {
        private val feelingsMap = mutableMapOf<Int, Feeling>()
    }

    private fun getContent(node: Element, tagName: String) : String {
        return node.getElementsByTagName(tagName).item(0).textContent
    }

    private fun getContentNullable(node: Element, tagName: String) : String? {
        val content = node.getElementsByTagName(tagName).item(0).textContent
        return if (content == "x") null else content
    }

    fun getFeelingById(id: Int) : Feeling? {
        return feelingsMap[id]
    }

    fun getFeelingByColor(color: Int) : Feeling? {
        return feelingsMap.values.firstOrNull() { feeling -> feeling.colorDec == color }
    }

    fun parseFeelingXml(context: Context) {

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val inputStream = context.resources.openRawResource(R.raw.feelings)
        val doc = builder.parse(inputStream)
        val feelingNodes = doc.getElementsByTagName("feeling")
        for (i in 0 until feelingNodes.length) {
            val node = feelingNodes.item(i) as Element
            val feeling = Feeling()
            feeling.id = getContent(node, "id").toInt()
            feeling.uniqueName = getContent(node, "unique_name")
            feeling.position = getContent(node, "position")
            feeling.originCenterLt = getContentNullable(node, "origin_center_lt")
            feeling.originInnerLt = getContentNullable(node, "origin_inner_lt")
            feeling.nameLt = getContent(node, "name_lt")
            feeling.originCenterEn = getContentNullable(node, "origin_center_en")
            feeling.originInnerEn = getContentNullable(node, "origin_inner_en")
            feeling.nameEn = getContent(node, "name_en")
            feeling.colorDec = getContent(node, "color_dec").toInt()
            feeling.colorHex = getContent(node, "color_hex")
            feeling.colorR = getContent(node, "color_r").toInt()
            feeling.colorG = getContent(node, "color_g").toInt()
            feeling.colorB = getContent(node, "color_b").toInt()
            feeling.colorARGB = Color.argb(255, feeling.colorR, feeling.colorG, feeling.colorB)
            feelingsMap[feeling.id] = feeling
        }
    }
}