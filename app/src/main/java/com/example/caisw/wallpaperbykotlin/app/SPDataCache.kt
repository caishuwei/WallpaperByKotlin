package com.example.caisw.wallpaperbykotlin.app

import android.content.Context
import android.content.SharedPreferences
import com.example.caisw.wallpaperbykotlin.core.ProjectFactory

/**
 * SharedPreferences数据缓存,在Presenter View中使用时不要自己去实例化，而应该通过组件注入的方式取得唯一实例
 */
class SPDataCache constructor(private val context: Context) {

    private val sharedPreferencesMap = HashMap<String, SharedPreferences>()
    val common = Common(this)

    fun getSharedPreferencesEditor(name: String, mode: Int = Context.MODE_PRIVATE): SharedPreferences.Editor {
        return getSharedPreferences(name, mode).edit()
    }

    fun getSharedPreferences(name: String, mode: Int = Context.MODE_PRIVATE): SharedPreferences {
        val sp = sharedPreferencesMap[name]
        return if (sp == null) {
            val newSharedPreferences = context.getSharedPreferences(name, mode)
            sharedPreferencesMap[name] = newSharedPreferences
            newSharedPreferences
        } else {
            sp
        }
    }
}

/**
 * SharedPreferences基本的存储操作，提交采用异步提交apply，但它缓存在内存里的记录应该是实时的
 */
open class BaseOperation(private val spDataCache: SPDataCache, private val fileName: String) {

    fun putString(key: String, value: String?) {
        spDataCache.getSharedPreferencesEditor(fileName).putString(key, value).apply()
    }

    fun getString(key: String, def: String? = null): String? {
        return spDataCache.getSharedPreferences(fileName).getString(key, def)
    }

    fun putBoolean(key: String, value: Boolean) {
        spDataCache.getSharedPreferencesEditor(fileName).putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, def: Boolean = false): Boolean {
        return spDataCache.getSharedPreferences(fileName).getBoolean(key, def)
    }

    fun putInt(key: String, value: Int) {
        spDataCache.getSharedPreferencesEditor(fileName).putInt(key, value).apply()
    }

    fun getInt(key: String, def: Int = 0): Int {
        return spDataCache.getSharedPreferences(fileName).getInt(key, def)
    }

    fun putLong(key: String, value: Long) {
        spDataCache.getSharedPreferencesEditor(fileName).putLong(key, value).apply()
    }

    fun getLong(key: String, def: Long = 0L): Long {
        return spDataCache.getSharedPreferences(fileName).getLong(key, def)
    }

    fun putFloat(key: String, value: Float) {
        spDataCache.getSharedPreferencesEditor(fileName).putFloat(key, value).apply()
    }

    fun getFloat(key: String, def: Float = 0F): Float {
        return spDataCache.getSharedPreferences(fileName).getFloat(key, def)
    }
}

/**
 * 通用数据存储
 */
class Common(spDataCache: SPDataCache) : BaseOperation(spDataCache, "Common") {

    companion object {
        private val KEY_STRING_WALLPAPER_TYPE = "壁纸类型"
    }

    fun setWallPaperType(wallPaperType: String) {
        putString(KEY_STRING_WALLPAPER_TYPE, wallPaperType)
    }

    fun getWallPaperType(): String {
        return when (getString(KEY_STRING_WALLPAPER_TYPE, ProjectFactory.TAG_LIFE_GAME_WALLPAPER)) {
            ProjectFactory.TAG_PICTURE_WALLPAPER -> ProjectFactory.TAG_PICTURE_WALLPAPER
            else -> ProjectFactory.TAG_LIFE_GAME_WALLPAPER
        }
    }
}

