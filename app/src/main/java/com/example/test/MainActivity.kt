package com.example.test

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.test.databinding.ActivityMainBinding
import com.example.test.fragment.CameraFragment
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList
import com.google.mediapipe.framework.ProtoUtil
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult


class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
//    private lateinit var overlayView: OverlayView
    private val viewModel: MainViewModel by viewModels()
    private var cameraFragment: CameraFragment? = null
    private lateinit var poseLandmarkerHelper: PoseLandmarkerHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        ProtoUtil.registerTypeName(
            NormalizedLandmarkList::class.java,
            "mediapipe.NormalizedLandmarkList"
        )
//        poseLandmarkerHelper = PoseLandmarkerHelper(
//            minPoseDetectionConfidence = 0.5f, // 设置置信度阈值
//            minPoseTrackingConfidence = 0.5f, // 设置追踪置信度阈值
//            minPosePresenceConfidence = 0.5f, // 设置存在置信度阈值
//            currentModel = PoseLandmarkerHelper.MODEL_POSE_LANDMARKER_FULL, // 选择模型
//            currentDelegate = PoseLandmarkerHelper.DELEGATE_CPU, // 选择硬件代理
//            runningMode = RunningMode.LIVE_STREAM, // 设置运行模式
//            context = this // 传递上下文
//        )
        cameraFragment = supportFragmentManager.findFragmentById(R.id.camera_fragment) as? CameraFragment

        if (cameraFragment == null) {

            cameraFragment = CameraFragment()
            cameraFragment!!.setListener { result ->
                displayPoseLandmarkerResult(result)
            }
            fragmentTransaction.replace(R.id.camera_fragment_container, cameraFragment!!)
            fragmentTransaction.commit()
        }
//        poseLandmarkerHelper.detectLiveStream(imageProxy, true)
        // 模拟PoseLandmarkerResult对象
        val poseLandmarkerResult = getPoseLandmarkerResult()

        if (poseLandmarkerResult != null) {
            Log.d("MainActivity", "PoseLandmarkerResult initialized successfully")

            displayPoseLandmarkerResult(poseLandmarkerResult)
        } else {
            Log.e("MainActivity", "PoseLandmarkerResult is null")
        }
    }

    private fun getPoseLandmarkerResult(): PoseLandmarkerResult? {
        // 模拟PoseLandmarkerResult对象
        // 你应该将其替换为从相机中获取的真实对象
        return null
    }

    private fun displayPoseLandmarkerResult(poseLandmarkerResult: PoseLandmarkerResult) {
        val landmarks = poseLandmarkerResult.landmarks()
        if (landmarks != null) {
            // 遍历每个姿势
            for (i in landmarks.indices) {
                val landmarkList = landmarks[i]

                println("PoseLandmarkerResult:")
                println("Landmarks for Pose #$i:")

                // 遍历每个姿势的标记点
                for (j in landmarkList.indices) {
                    val landmark = landmarkList[j]

                    val x = landmark.x()
                    val y = landmark.y()
                    val z = landmark.z()
                    val visibility = landmark.visibility()
                    val presence = landmark.presence()

                    println("Landmark #$j:")
                    println("x: $x")
                    println("y: $y")
                    println("z: $z")
                    println("visibility: $visibility")
                    println("presence: $presence")
                }
            }
        }
    }
// 将摄像头帧数据转换为 Bitmap




    override fun onBackPressed() {
        finish()
    }
}
