package com.example.achieveit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hosseiniseyro.apprating.AppRatingDialog


class SettingFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =inflater.inflate(R.layout.fragment_setting, container, false)

        view.findViewById<TextView>(R.id.rating).setOnClickListener {

            buildRatingDialog()?.show();
        }

        return view
    }

    private fun buildRatingDialog(): AppRatingDialog? {
        return activity?.let {
            AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNeutralButtonText("Later")
                .setNoteDescriptions(
                    mutableListOf<String>(
                        "Very Bad",
                        "Not good",
                        "Quite ok",
                        "Very Good",
                        "Excellent !!!"
                    )
                )
                .setDefaultRating(2)
                .setThreshold(0)
                .setAfterInstallDay(0)
                .setTitle("Rate this application")
                .setDescription("Please select some stars and give your feedback")
                .setDefaultRating(2)
                .setThreshold(4)
                .setAfterInstallDay(0)
                .setNumberOfLaunches(3)
                .setRemindInterval(2)
                .setTitle("Rate this application")
                .setDescription("Please select some stars and give your feedback")
                .setStarColor(R.color.starColor)
                .setNoteDescriptionTextColor(R.color.noteDescriptionTextColor)
                .setTitleTextColor(R.color.titleTextColor)
                .setDescriptionTextColor(R.color.descriptionTextColor)
                .setCommentTextColor(R.color.commentTextColor)
                .setCommentBackgroundColor(R.color.white)
                .setDialogBackgroundColor(R.color.white)
                .setWindowAnimation(R.style.MyDialogSlideHorizontalAnimation)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.hintTextColor)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(it)
        }
    }

}