package com.example.caisw.wallpaperbykotlin.app

import android.app.Application
import android.content.Context
import android.os.Process
import android.text.TextUtils
import androidx.multidex.MultiDex
import com.example.caisw.wallpaperbykotlin.BuildConfig
import com.tencent.bugly.crashreport.CrashReport
import java.io.BufferedReader
import java.io.FileReader

/**
 * 自定义应用类，用于提供跟随整个应用生命周期的全局变量
 * Created by caisw on 2018/3/2.
 */
class MyApplication : Application() {

    companion object {
        /**应用实例*/
        lateinit var instance: MyApplication

    }

    override fun onCreate() {
        super.onCreate()
        MyApplication.instance = this
        initBuglyInMainProcess()
    }

    private fun initBuglyInMainProcess() {
//        val packageName = packageName
//        val processName = getCurrProcessName(Process.myPid())
//        val userStrategy = CrashReport.UserStrategy(this)
//        userStrategy.isUploadProcess = processName == null || processName.equals(packageName)
//        CrashReport.initCrashReport(this, "d736acce99", BuildConfig.DEBUG, userStrategy)
        CrashReport.initCrashReport(this, "d736acce99", BuildConfig.DEBUG)
    }

    /**
     * 官方提供的读取进程名称的方法是错的。。。
     */
    private fun getCurrProcessName(myPid: Int): String? {
        var bufferedReader: BufferedReader? = null
        try {
            bufferedReader = BufferedReader(FileReader("/proc/$myPid/cmdline"))
            var processName = bufferedReader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim()
            }
            return processName
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                bufferedReader?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }
}