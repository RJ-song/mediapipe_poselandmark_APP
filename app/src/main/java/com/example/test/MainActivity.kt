
package com.example.test

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.test.databinding.ActivityMainBinding
import com.example.test.fragment.CameraFragment
import com.example.test.ml.Squat
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList
import com.google.mediapipe.framework.ProtoUtil
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import android.widget.Chronometer
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    //    private lateinit var overlayView: OverlayView
    private val viewModel: MainViewModel by viewModels()
    private var cameraFragment: CameraFragment? = null
    private var testFrame: FrameLayout? = null
    private var testView: View? = null
    private lateinit var poseLandmarkerHelper: PoseLandmarkerHelper
    private lateinit var countText: TextView
    private lateinit var cm : Chronometer
    private lateinit var sportClass:TextView
    private lateinit var preClass: String
    private var count: Int =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(activityMainBinding.root)

        count = 0
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        ProtoUtil.registerTypeName(
            NormalizedLandmarkList::class.java,
            "mediapipe.NormalizedLandmarkList"
        )
        cameraFragment = supportFragmentManager.findFragmentById(R.id.camera_fragment) as? CameraFragment

        testFrame = findViewById(R.id.test_fragment_container)
        val layoutInflater = LayoutInflater.from(this)
        testView = layoutInflater.inflate(R.layout.state,  null)
        testFrame?.addView(testView)
        sportClass = findViewById(R.id.sportClass)
        cm = findViewById(R.id.timer)
        cm.start()

//        val handler = Handler(Looper.getMainLooper())
//
//        val updateTextRunnable = object : Runnable {
//            override fun run() {
//                countText.text = count.toString()
//                handler.postDelayed(this, 1000)
//            }
//        }
//        handler.post(updateTextRunnable)

        if (cameraFragment == null) {

            cameraFragment = CameraFragment()
            cameraFragment!!.setListener { result ->
                processResult(result)
            }
            fragmentTransaction.replace(R.id.camera_fragment_container, cameraFragment!!)
            fragmentTransaction.commit()
        }

    }


//    private fun displayPoseLandmarkerResult(poseLandmarkerResult: PoseLandmarkerResult) {
//        val landmarks = poseLandmarkerResult.landmarks()
//        if (landmarks != null) {
//            // 遍历每个姿势
//            for (i in landmarks.indices) {
//                val landmarkList = landmarks[i]
//
//                println("PoseLandmarkerResult:")
//                println("Landmarks for Pose #$i:")
//
//                // 遍历每个姿势的标记点
//                for (j in landmarkList.indices) {
//                    val landmark = landmarkList[j]
//
//                    val x = landmark.x()
//                    val y = landmark.y()
//                    val z = landmark.z()
//                    val visibility = landmark.visibility()
//                    val presence = landmark.presence()
//
//                    println("Landmark #$j:")
//                    println("x: $x")
//                    println("y: $y")
//                    println("z: $z")
//                    println("visibility: $visibility")
//                    println("presence: $presence")
//                }
//            }
//        }
//    }

    private fun processResult(poseLandmarkerResult: PoseLandmarkerResult) {
        val landmarks = poseLandmarkerResult.landmarks()

        if (landmarks != null) {
            val model = Squat.newInstance(this)
            for (i in landmarks.indices) {
                val landmarkList = landmarks[i]
                println("PoseLandmarkerResult:")
                println("Landmarks for Pose #$i:")

                // Create a ByteBuffer for the model input
                val inputBuffer = ByteBuffer.allocateDirect(1 * 132 * 4) // 1x132 float32 values (4 bytes each)

                for (j in landmarkList.indices) {
                    val landmark = landmarkList[j]

                    // Convert Landmark data to ByteBuffer
                    inputBuffer.putFloat(landmark.x().toFloat())
                    inputBuffer.putFloat(landmark.y().toFloat())
                    inputBuffer.putFloat(landmark.z().toFloat())

                    val visibility = landmark.visibility()
                    if (visibility.isPresent) {
                        inputBuffer.putFloat(visibility.get())
                    } else {
                        println("no visibility available")
                    }
                }

                // Now, you can use this ByteBuffer as input to your model
                inputBuffer.rewind()
                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 132), DataType.FLOAT32)
                inputFeature0.loadBuffer(inputBuffer)

                // Runs model inference and gets result.
                val outputs = model.process(inputFeature0)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer
                // Get the model's prediction (0 or 1)
                val prediction = if (outputFeature0.getFloatValue(0) > outputFeature0.getFloatValue(1)) {
                    "down"
                } else {
                    "up"
                }
                sportClass.text=prediction
                println("Model Prediction: $prediction")
                // Process the model output as needed
            }
            // Releases model resources if no longer used.
            model.close()
        }
    }
    override fun onBackPressed() {
        finish()
    }
}
