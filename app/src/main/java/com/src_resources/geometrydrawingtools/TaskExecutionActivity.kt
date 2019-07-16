package com.src_resources.geometrydrawingtools

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import android.widget.TextView
import java.lang.ref.WeakReference

//@Deprecated("This class is hard to implement and it needs to use Intent. "
//        + "Please use AlertDialog instead.")
class TaskExecutionActivity : AppCompatActivity() {

    companion object {
        //
        // Constant values for Intent extra data
        //
        const val EXTRA__TASK_NAME = "extra_taskName"
//        const val EXTRA__TASK_CALLABLE = "extra_taskCallable"
//        const val EXTRA__TASK_EXECUTION_HANDLE = "extra_taskExecutionHandle"
        const val EXTRA__PROGRESS = "extra_progress"

        //
        // Constant values for broadcast actions and categories
        //
        const val ACTION__TASK = "com.src_resources.geometrydrawingtools.TaskExecutionActivity.ACTION__TASK"
        const val CATEGORY__UPDATE_PROGRESS = "com.src_resources.geometrydrawingtools.TaskExecutionActivity.CATEGORY__UPDATE_PROGRESS"
        const val CATEGORY__FINISH = "com.src_resources.geometrydrawingtools.TaskExecutionActivity.CATEGORY__FINISH"

        //
        // Constant values for Message.what
        //
        private const val MESSAGE_WHAT__UPDATE_PROGRESS = 1
        private const val MESSAGE_WHAT__EXIT = 2
    }

//    abstract class TaskCallable() : Callable<Boolean>, Parcelable {
//        private data class Wrapper(val progressUpdatingFunc: ((Int) -> Unit)?) : Serializable
//
//        private var progressUpdatingFunc: ((Int) -> Unit)? = null
//
//        protected constructor(parcelObj: Parcel) : this() {
//            val wrapper = parcelObj.readSerializable() as Wrapper
//            progressUpdatingFunc = wrapper.progressUpdatingFunc
//        }
//
//        @CallSuper
//        override fun writeToParcel(dest: Parcel?, flags: Int) {
//            if (dest == null)
//                throw IllegalArgumentException("The Parcel argument is null.")
//            val wrapper = Wrapper(progressUpdatingFunc)
//            dest.writeValue(wrapper)
//        }
//
//        override fun describeContents(): Int = 0
//
//        fun updateProgress(progress: Int) {
//            progressUpdatingFunc?.invoke(progress)
//        }
//
//        internal fun attachProgressUpdatingFunction(func : (Int) -> Unit) {
//            progressUpdatingFunc = func
//        }
//    }

//    class DefaultTaskCallable(val taskFunc : ((Int) -> Unit) -> Boolean) : TaskCallable() {
//        private data class Wrapper(val taskFunc : ((Int) -> Unit) -> Boolean) : Serializable
//
//        constructor(parcel: Parcel) : this() {
//        }
//
//        override fun call(): Boolean = taskFunc(this::updateProgress)
//
//        override fun writeToParcel(parcel: Parcel, flags: Int) {
//            super.writeToParcel(parcel, flags)
//        }
//
//        companion object CREATOR : Parcelable.Creator<DefaultTaskCallable> {
//            override fun createFromParcel(parcel: Parcel): DefaultTaskCallable {
//                return DefaultTaskCallable(parcel)
//            }
//
//            override fun newArray(size: Int): Array<DefaultTaskCallable?> {
//                return arrayOfNulls(size)
//            }
//        }
//    }

//    class TaskExecutionHandle : Serializable {
//        private var progressUpdatingFunc: SerializableFunction1<Int, Unit>? = null
//
//        fun updateProgress(progress: Int) = progressUpdatingFunc?.invoke(progress)
//
//        internal fun attachProgressUpdatingFunction(func: SerializableFunction1<Int, Unit>) {
//            progressUpdatingFunc = func
//        }
//    }

    private class MyHandler(outerClassObj: TaskExecutionActivity) : Handler() {
        private val outerClassWeakRef = WeakReference(outerClassObj)
        private val outerClassObj: TaskExecutionActivity
                get() = outerClassWeakRef.get() ?: throw NullPointerException(
                            "The outer class object has already destroyed by GC.")

        override fun handleMessage(msg: Message?) {
            if (msg == null)
                throw IllegalArgumentException("Use null object to call this method.")
            when (msg.what) {
                MESSAGE_WHAT__UPDATE_PROGRESS -> {
                    val progress = msg.arg1
                    outerClassObj.taskProgressBar.progress = progress
                }
                MESSAGE_WHAT__EXIT -> {
                    outerClassObj.exit()
                }
            }
        }
    }

    private class MyBroadcastReceiver(val outerClassObj: TaskExecutionActivity) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null)
                throw IllegalArgumentException("Argument \"intent\" is null.")
            if (intent.categories.contains(CATEGORY__UPDATE_PROGRESS)) {
                val progress = intent.getIntExtra(EXTRA__PROGRESS, 0)
                outerClassObj.updateProgress(progress)
            }
            if (intent.categories.contains(CATEGORY__FINISH)) {
                outerClassObj.exit()
            }
        }
    }

//    private inner class TaskThread : Thread() {
//        init {
//            name = "TaskThread-$id"
//        }
//
//        override fun run() {
//            val result = taskCallableObj.call()
//            handler.sendEmptyMessage(MESSAGE_WHAT__EXIT)
//        }
//    }

    private lateinit var taskNameTextView: TextView
    private lateinit var taskProgressBar: ProgressBar

//    private lateinit var taskCallableObj: TaskCallable
//    private lateinit var taskExecutionHandleObj: TaskExecutionHandle
    private val handler = MyHandler(this)
//    private val taskThread = TaskThread()
    private val broadcastReceiver = MyBroadcastReceiver(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_execution)

        taskNameTextView = findViewById(R.id.taskNameTextView)
        taskNameTextView.text = intent.getStringExtra(EXTRA__TASK_NAME)
        taskProgressBar = findViewById(R.id.taskProgressBar)

//        taskCallableObj = intent.getSerializableExtra(EXTRA__TASK_CALLABLE) as TaskCallable
//        taskCallableObj.attachProgressUpdatingFunction(this::updateProgress)
//        taskExecutionHandleObj = intent.getSerializableExtra(EXTRA__TASK_EXECUTION_HANDLE)
//                as TaskExecutionHandle
//        taskExecutionHandleObj.attachProgressUpdatingFunction(
//                object : SerializableFunction1<Int, Unit> {
//                    override fun invoke(progress: Int) {
//                        updateProgress(progress)
//                    }
//                })
//        val applicationObj = application as MainApplication
//        applicationObj.activityExtra_TaskExecutionActivity_taskProgressUpdatingFunc = this::updateProgress
//        applicationObj.activityExtra_TaskExecutionActivity_taskFinishingFunc = this::exit

//        taskThread.start()
    }

    override fun onResume() {
        registerMyBroadcastReceiver()
        super.onResume()
    }

    override fun onPause() {
        unregisterMyBroadcastReceiver()
        super.onPause()
    }

    private fun registerMyBroadcastReceiver() {
        val filter = IntentFilter(ACTION__TASK)
        filter.addCategory(CATEGORY__UPDATE_PROGRESS)
        filter.addCategory(CATEGORY__FINISH)
        registerReceiver(broadcastReceiver, filter)
    }

    private fun unregisterMyBroadcastReceiver() {
        unregisterReceiver(broadcastReceiver)
    }

    private fun updateProgress(progress: Int) {
        handler.obtainMessage(MESSAGE_WHAT__UPDATE_PROGRESS).let {
            it.arg1 = progress
            handler.sendMessage(it)
        }
    }

    private fun exit() {
        finish()
    }
}
