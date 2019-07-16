package com.src_resources.geometrydrawingtools

import android.content.Intent
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.Serializable
import java.util.concurrent.locks.LockSupport

//private val WIDTH = 768
//private val HEIGHT = 320
//private val X_OFFSET = 5

class AppMainActivity : AppCompatActivity(), Serializable {

//    private class MyTaskCallable(val outerClassObj: AppMainActivity) : TaskExecutionActivity.TaskCallable() {
//        companion object CREATOR : Parcelable.Creator<MyTaskCallable>
//
//        override fun call(): Boolean {
//            Thread.sleep(2000)
//            outerClassObj.initMainSurfaceView()
//            return true
//        }
//    }

    private lateinit var mainSurfaceView: SurfaceView
    private var hasSurface = false
    private lateinit var mainSurfaceViewHolder: SurfaceHolder
    private lateinit var mPath: Path
    private lateinit var mPaint: Paint
//    private var lastX = 0f
//    private var lastY = 0f
//    private var cx = X_OFFSET
//    private val centerY = HEIGHT / 2
    private val handler = Handler()
//    private val taskHandle = TaskExecutionActivity.TaskExecutionHandle()
//    private lateinit var taskProgressUpdatingFunc: (Int) -> Unit
//    private lateinit var taskFinishingFunc: () -> Unit
    private val activityInitializingThread = Thread {
        // Wait for mainSurfaceViewHolder to be created.
        LockSupport.park()
        // Call mainSurfaceView initializing method.
        initMainSurfaceView()
        // Finish the task.
        finishTask()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_main)

        mainSurfaceView = findViewById(R.id.mainSurfaceView)
        mainSurfaceViewHolder = mainSurfaceView.holder
        mainSurfaceViewHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {
                hasSurface = true
                LockSupport.unpark(activityInitializingThread)
            }

            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                hasSurface = false
            }
        })

        mPath = Path()
        mPaint = Paint()
        mPaint.color = Color.RED
        mPaint.strokeWidth = 10f
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeJoin = Paint.Join.BEVEL

//        doActivityCreatingTask()
        val intentObj = Intent(this, TaskExecutionActivity::class.java)
        intentObj.putExtra(TaskExecutionActivity.EXTRA__TASK_NAME,
                resources.getString(R.string.launching))
//        intentObj.putExtra(TaskExecutionActivity.EXTRA__TASK_CALLABLE,
//                object : TaskExecutionActivity.TaskCallable() {
//                    override fun call(): Boolean {
//                        Thread.sleep(2000)
//                        initMainSurfaceView()
//                        return true
//                    }
//                })
//        intentObj.putExtra(TaskExecutionActivity.EXTRA__TASK_EXECUTION_HANDLE, taskHandle)
        startActivity(intentObj)

        activityInitializingThread.start()
    }

    private fun initMainSurfaceView() {
        val canvas = mainSurfaceViewHolder.lockCanvas()
        canvas.drawColor(Color.WHITE)
        mainSurfaceViewHolder.unlockCanvasAndPost(canvas)
    }

    private fun updateTaskProgress(progress: Int) {
        Intent().let {
            it.action = TaskExecutionActivity.ACTION__TASK
            // Please note that it's wrong to write like:
            // it.categories.add(TaskExecutionActivity.CATEGORY__UPDATE_PROGRESS)
            it.addCategory(TaskExecutionActivity.CATEGORY__UPDATE_PROGRESS)
            it.putExtra(TaskExecutionActivity.EXTRA__PROGRESS, progress)
            sendBroadcast(it)
        }
    }

    private fun finishTask() {
        Intent().let {
            it.action = TaskExecutionActivity.ACTION__TASK
            // Please note that it's wrong to write like:
            // it.categories.add(TaskExecutionActivity.CATEGORY__FINISH)
            it.addCategory(TaskExecutionActivity.CATEGORY__FINISH)
            sendBroadcast(it)
        }
    }

//    private fun doActivityCreatingTask() {
//        Thread {
//            Thread.sleep(2000)
//            handler.post {
//                val intentObj = Intent(this, TaskExecutionActivity::class.java)
//                intentObj.putExtra(TaskExecutionActivity.EXTRA__TASK_NAME,
//                        resources.getString(R.string.launching))
//                intentObj.putExtra(TaskExecutionActivity.EXTRA__TASK_CALLABLE,
//                        MyTaskCallable(this))
//                startActivity(intent)
//            }
//        }.start()
//    }
}
