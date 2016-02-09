package pl.prabel.kotlindemo.gson

import com.google.common.base.Objects
import com.google.gson.FieldNamingStrategy

import java.lang.reflect.Field

class AndroidUnderscoreNamingStrategy : FieldNamingStrategy {

    override fun translateName(field: Field): String {
        val name = field.name
        val translation = StringBuilder()
        var i = 0
        if (name.length >= 2) {
            if ('m' == name[0] && Character.isUpperCase(name[1])) {
                i++
            }
        }
        while (i < name.length) {
            val character = name[i]
            if (Character.isUpperCase(character) && translation.length != 0) {
                translation.append("_")
            }
            translation.append(character)
            i++
        }
        val translated = translation.toString().toLowerCase()
        if (Objects.equal("guid", translated)) {
            return "id"
        }
        return translated
    }
}
