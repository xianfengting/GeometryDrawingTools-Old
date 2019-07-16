package com.src_resources.geometrydrawingtools

import android.app.Application

class MainApplication : Application() {
    //////////////////// Activity extra data ////////////////////
    ////////// .TaskExecutionActivity //////////
    var activityExtra_TaskExecutionActivity_taskProgressUpdatingFunc: ((Int) -> Unit)? = null
    var activityExtra_TaskExecutionActivity_taskFinishingFunc: (() -> Unit)? = null
}
