package com.src_resources.geometrydrawingtools

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.Serializable
import java.util.concurrent.locks.LockSupport

class AppMainActivity : AppCompatActivity(), Serializable {

    private class MyBroadcastReceiver(val outerClassObj: AppMainActivity) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null)
                throw IllegalArgumentException("Argument \"intent\" is null.")
            when (intent.action) {
                TaskExecutionActivity.ACTION__CALLBACK__ON_ACTIVITY_INITIALIZED -> {
                    LockSupport.unpark(outerClassObj.activityInitializingThread)
                }
            }
        }
    }

    private lateinit var mainSurfaceView: SurfaceView
    private var hasSurface = false
    private lateinit var mainSurfaceViewHolder: SurfaceHolder
    private lateinit var mPath: Path
    private lateinit var mPaint: Paint
    private val handler = Handler()
    private val broadcastReceiver = MyBroadcastReceiver(this)
    private val activityInitializingThread = Thread {
        fun doProgress() {
            for (i in 1..100) {
                Thread.sleep(50)
                updateTaskProgress(i)
                updateSubprogramName("进度:$i")
            }
        }
        // Wait for mainSurfaceViewHolder to be created and TaskExecutionActivity to be initialized.
        LockSupport.park()
        LockSupport.park()
        // Call mainSurfaceView initializing method.
        initMainSurfaceView()
        //
        doProgress()
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

        val intentObj = Intent(this, TaskExecutionActivity::class.java)
        intentObj.putExtra(TaskExecutionActivity.EXTRA__TASK_NAME,
                resources.getString(R.string.launching))
        startActivity(intentObj)

        activityInitializingThread.start()

        registerMyBroadcastReceiver()
    }

    override fun onDestroy() {
        unregisterMyBroadcastReceiver()
        super.onDestroy()
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

    private fun updateSubprogramName(name: String) {
        Intent(TaskExecutionActivity.ACTION__TASK).let {
            it.addCategory(TaskExecutionActivity.CATEGORY__UPDATE_SUBPROGRAM)
            it.putExtra(TaskExecutionActivity.EXTRA__SUBPROGRAM_NAME, name)
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

    private fun registerMyBroadcastReceiver() {
        val filter = IntentFilter(TaskExecutionActivity.ACTION__CALLBACK__ON_ACTIVITY_INITIALIZED)
        registerReceiver(broadcastReceiver, filter)
    }

    private fun unregisterMyBroadcastReceiver() {
        unregisterReceiver(broadcastReceiver)
    }
}
