package com.example.caisw.wallpaperbykotlin.utils

import android.content.res.Resources

/**
 * Created by caisw on 2018/3/12.
 */
class ScreenInfo {

    companion object {
        /**
         * 屏幕宽
         */
        val WIDTH = Resources.getSystem().displayMetrics.widthPixels
        /**
         * 屏幕高
         */
        val HEIGHT = Resources.getSystem().displayMetrics.heightPixels
        /**
         * 屏幕密度值1dp = Xpx
         */
        val DENSITY = Resources.getSystem().displayMetrics.density
        val SCALED_DENSITY = Resources.getSystem().displayMetrics.scaledDensity

        /**
         * dp->px
         */
        fun dp2px(value: Float): Float {
            return DENSITY * value
        }

        /**
         * px->dp
         */
        fun px2dp(value: Float): Float {
            return value / DENSITY
        }

        /**
         * sp->px
         */
        fun sp2px(value: Float): Float {
            return SCALED_DENSITY * value
        }

        /**
         * px->sp
         */
        fun px2sp(value: Float): Float {
            return value / SCALED_DENSITY
        }
    }
}
