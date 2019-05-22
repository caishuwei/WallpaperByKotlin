package com.example.caisw.wallpaperbykotlin.utils

class Utils {

    companion object {

        /**
         * 当参数不为空时运行
         */
        fun <A, B> runIfNotNull(a: A?, b: B?, run: (a: A, b: B) -> (Unit)) {
            if (a != null && b != null) {
                run(a, b)
            }
        }

    }

}