package com.olsonkaleb.allocationviewapp.ui.activity

import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.view.doOnLayout
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    /*
        I've only been testing things visually to see if the generated views match up to what I expect them to look like but I think they're accurate.
        If I were building this out properly I think I would use a custom layout rather than a FrameLayout and obviously move all of this related logic out of the activity and into it.
        And I'm guessing I'm not following best practices when it comes to the programmatic UI/View creation but it was a fun challenge and I learned a lot already figuring it out.
        Thanks for checking it out!
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = LinearLayout(this)
        rootLayout.orientation = LinearLayout.VERTICAL
        rootLayout.gravity = Gravity.CENTER_HORIZONTAL
        setContentView(rootLayout)

        //Create FrameLayouts to act as the allocations views.
        val frameLayout1 = FrameLayout(this)
        val layoutParams1 = FrameLayout.LayoutParams(400, 400)
        layoutParams1.setMargins(10, 10, 10, 10)
        rootLayout.addView(frameLayout1, layoutParams1)

        val frameLayout2 = FrameLayout(this)
        val layoutParams2 = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 400)
        layoutParams2.setMargins(10, 10, 10, 10)
        rootLayout.addView(frameLayout2, layoutParams2)

        val resetButton = Button(this)
        resetButton.text = "Reset"
        resetButton.setOnClickListener {
            frameLayout1.removeAllViews()
            frameLayout2.removeAllViews()
            iterateTreeView(frameLayout1, getRandomListOfIntegers(5, 15), Rect(0, 0, frameLayout1.width, frameLayout1.height), true)
            iterateTreeView(frameLayout2, getRandomListOfIntegers(15, 25), Rect(0, 0, frameLayout2.width, frameLayout2.height), true)
        }
        rootLayout.addView(resetButton)

        //Build the allocation views once things are ready
        rootLayout.doOnLayout {
            iterateTreeView(frameLayout1, getRandomListOfIntegers(5, 15), Rect(0, 0, frameLayout1.width, frameLayout1.height), true)
            iterateTreeView(frameLayout2, getRandomListOfIntegers(15, 25), Rect(0, 0, frameLayout2.width, frameLayout2.height), true)
        }
    }

    //Generate some random data
    private fun getRandomListOfIntegers(min: Int, max: Int): ArrayList<Int> = ArrayList((1..Random.nextInt(min, max)).map { Random.nextInt(10) })

    private fun iterateTreeView(parentView: ViewGroup, values: ArrayList<Int>, bounds: Rect, verticalDivide: Boolean) {
        if (values.size == 1) {
            //If we're down to one element left then draw it into the current remaining bounds
            val newTreeElement = View(this)
            //Assign some random colors
            newTreeElement.setBackgroundColor(Color.rgb(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()))
            val layoutParams = FrameLayout.LayoutParams(bounds.width(), bounds.height())
            layoutParams.setMargins(bounds.left, bounds.top, 0, 0)
            newTreeElement.layoutParams = layoutParams
            parentView.addView(newTreeElement)
        }
        else {
            //Split list into roughly equal halves. I wouldn't be surprised if there's a faster mathy way to do this but I don't know it off the top of my head.
            val total = values.sum()
            var splittingTotal = 0
            var splittingIndex = 0
            for (i in 0 until values.size - 1) {
                splittingTotal += values[i]
                if (splittingTotal >= total / 2) {
                    splittingIndex = i
                    break
                }
            }

            val firstPartitionList = ArrayList(values.subList(0, splittingIndex + 1))
            val secondPartitionList = ArrayList(values.subList(splittingIndex + 1, values.size))
            val firstPartitionBounds: Rect
            val secondPartitionBounds: Rect

            //Find partition bounds based on which direction we're splitting in
            if (verticalDivide) {
                val firstPartitionWidth = ((firstPartitionList.sum() / total.toFloat()) * bounds.width()).toInt()
                firstPartitionBounds = Rect(bounds.left, bounds.top, bounds.left + firstPartitionWidth, bounds.bottom)
                secondPartitionBounds = Rect(bounds.left + firstPartitionWidth, bounds.top, bounds.right, bounds.bottom)
            }
            else {
                val firstPartitionHeight = ((firstPartitionList.sum() / total.toFloat()) * bounds.height()).toInt()
                firstPartitionBounds = Rect(bounds.left, bounds.top, bounds.right, bounds.top + firstPartitionHeight)
                secondPartitionBounds = Rect(bounds.left, bounds.top + firstPartitionHeight, bounds.right, bounds.bottom)
            }

            //Continue iterating until we reach the case where we have 1 element remaining
            iterateTreeView(parentView, firstPartitionList, firstPartitionBounds, !verticalDivide)
            iterateTreeView(parentView, secondPartitionList, secondPartitionBounds, !verticalDivide)
        }
    }
}