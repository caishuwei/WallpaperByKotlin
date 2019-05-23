package com.example.caisw.wallpaperbykotlin.ui.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.caisw.wallpaperbykotlin.R

class CommonFragmentActivity : BaseActivity() {

    companion object {
        private const val FRAGMENT_TAG = "CommonFragmentActivity_Fragment"

        fun openActivity(context: Context, clazz: Class<*>, data: Bundle?) {
            val intent = Intent(context, CommonFragmentActivity::class.java)
            intent.putExtra("clazz", clazz)
            if (data != null) {
                intent.putExtras(data)
            }
            context.startActivity(intent)
        }
    }

    override fun getContentViewResId(): Int {
        return R.layout.activity_common_fragment
    }

    override fun initData() {
        super.initData()
        val clazz = intent.getSerializableExtra("clazz")
        if (clazz != null && clazz is Class<*>) {
            var fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
            if (fragment == null) {
                try {
                    val instance = clazz.newInstance()
                    if (instance is Fragment) {
                        instance.arguments = intent.extras
                        fragment = instance
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (fragment != null) {
                if (!fragment.isAdded) {
                    supportFragmentManager.beginTransaction()
                            .add(R.id.fl_fragment_container, fragment, FRAGMENT_TAG)
                            .commitAllowingStateLoss()
                }
                return
            }
        }
        finish()
    }
}