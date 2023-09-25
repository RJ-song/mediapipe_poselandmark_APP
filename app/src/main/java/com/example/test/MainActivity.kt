package com.example.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.test.databinding.ActivityMainBinding
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import com.example.test.MainViewModel
import com.example.test.fragment.CameraFragment
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var poseLandmarkerResult: PoseLandmarkerResult? = null
    private var cameraFragment: CameraFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
//        val navController = navHostFragment.navController
//        activityMainBinding.navigation.setupWithNavController(navController)
//        activityMainBinding.navigation.setOnNavigationItemReselectedListener {
//            // ignore the reselection
//        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        // 找到 並引用CameraFragment
        cameraFragment = supportFragmentManager.findFragmentById(R.id.camera_fragment) as? CameraFragment

        val cameraFragment = CameraFragment()

        fragmentTransaction.replace(R.id.camera_fragment_container, cameraFragment)
        fragmentTransaction.commit()


        val poseLandmarkerResult = cameraFragment?.getPoseLandmarkerResult()

        // 檢查poseLandmarkerResult是否為null，然後顯示姿勢關鍵點的資訊
        try {
            val landmarks = poseLandmarkerResult?.landmarks() ?: throw NullPointerException("poseLandmarkerResult is null")

            for (landmark in landmarks) {
                Log.d("PoseLandmark", "Landmark: ${landmark.toString()}")
            }
        } catch (e: NullPointerException) {
            Log.e("PoseLandmark", "An error occurred: ${e.message}")
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
