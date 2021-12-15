package com.swiftdating.app.ui.homeScreen.adapter

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.swiftdating.app.common.CommonUtils
import com.swiftdating.app.common.CustomPagerAdapter
import com.swiftdating.app.data.network.CallServer
import com.swiftdating.app.model.DetailTagModel
import com.swiftdating.app.model.responsemodel.ImageForUser
import com.swiftdating.app.model.responsemodel.User
import com.swiftdating.app.ui.homeScreen.fragment.FindMatchFragment
import com.swiftdating.app.ui.viewpagerScreen.ViewPagerActivity
import com.bumptech.glide.Glide
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.swiftdating.app.R
import com.swiftdating.app.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_insta_test.*

class CardDetailAdapter(
        var context: Context?,
        private var spots: List<User> = emptyList(),
        var height: Int,
        var listener: FindMatchFragment,
        val baseActivity: BaseActivity
) : RecyclerView.Adapter<CardDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.detailview, parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.minimumHeight = height
        holder.image.maxHeight = height

        holder.image2.maxHeight = 1700
        holder.image2.minimumHeight = 1700

        holder.image3.maxHeight = 1700
        holder.image3.minimumHeight = 1700

        holder.image4.maxHeight = 1700
        holder.image4.minimumHeight = 1700

        holder.image5.maxHeight = 1700
        holder.image5.minimumHeight = 1700

        holder.image6.maxHeight = 1700
        holder.image6.minimumHeight = 1700

        val spot = spots[position]

        holder.imageUser = ArrayList()
        holder.imageUser = spot.imageForUser.sortedWith(compareBy { it.orderId })
        Glide.with(holder.image).load(CallServer.BaseImage + holder.imageUser[0].imageUrl).centerCrop()
                .into(holder.image)

        if (!spot.selfieVerificationStatus.equals("Pending", ignoreCase = true) && !spot.selfieVerificationStatus.equals("No", ignoreCase = true)) {
            holder.ivVerifyLogo.visibility = VISIBLE
        } else {
            holder.ivVerifyLogo.visibility = GONE
        }

        if (spot.imageForUser.size > 1) {
            Glide.with(holder.image2).load(CallServer.BaseImage + holder.imageUser[1].imageUrl).centerCrop()
                    .into(holder.image2)
            if (spot.imageForUser.size > 2) {
                Glide.with(holder.image3).load(CallServer.BaseImage + holder.imageUser[2].imageUrl).centerCrop()
                        .into(holder.image3)
                if (spot.imageForUser.size > 3) {
                    Glide.with(holder.image4).load(CallServer.BaseImage + holder.imageUser[3].imageUrl).centerCrop()
                            .into(holder.image4)
                    if (spot.imageForUser.size > 4) {
                        Glide.with(holder.image5).load(CallServer.BaseImage + holder.imageUser[4].imageUrl).centerCrop()
                                .into(holder.image5)
                        if (spot.imageForUser.size > 5) {
                            Glide.with(holder.image6).load(CallServer.BaseImage + holder.imageUser[5].imageUrl).centerCrop()
                                    .into(holder.image6)
                        } else {
                            holder.image6.visibility = GONE
                        }
                    } else {
                        holder.image5.visibility = GONE
                        holder.image6.visibility = GONE
                    }
                } else {
                    holder.image4.visibility = GONE
                    holder.image5.visibility = GONE
                    holder.image6.visibility = GONE
                }
            } else {
                holder.image3.visibility = GONE
                holder.image4.visibility = GONE
                holder.image5.visibility = GONE
                holder.image6.visibility = GONE
            }
        } else {
            holder.image2.visibility = GONE
            holder.image3.visibility = GONE
            holder.image4.visibility = GONE
            holder.image5.visibility = GONE
            holder.image6.visibility = GONE
        }


//        if (spot.insta != null && spot.insta.isNotEmpty()) {
//            val pages: Vector<View> = Vector<View>()
//            holder.rlInstaGramView.visibility = VISIBLE
//
//            if (spot.insta.size > 0) {
//                val gridView1 = GridView(context)
//                pages.add(gridView1)
//                gridView1.numColumns = 3
//                gridView1.adapter = ArrayAdapter<String>(context!!, R.layout.insta_photo_grid_view, arrayOf("A1", "B1", "C1", "D1", "E1", "F1"))
//
//            }
//            if (spot.insta.size > 6) {
//                val gridView2 = GridView(context)
//                pages.add(gridView2)
//                gridView2.numColumns = 3
//                gridView2.adapter = ArrayAdapter<String>(context!!, R.layout.insta_photo_grid_view, arrayOf("A2", "B2", "C2", "D2", "E2", "F2"))
//            }
//            if (spot.insta.size > 12) {
//                val gridView3 = GridView(context)
//                pages.add(gridView3)
//                gridView3.numColumns = 3
//                gridView3.adapter = ArrayAdapter<String>(context!!, R.layout.insta_photo_grid_view, arrayOf("A3", "B3", "C3", "D3", "E3", "F3"))
//            }
//            holder.customPagerAdapter = CustomPagerAdapter(context, pages, spot.insta)
//            holder.viewPager.adapter = holder.customPagerAdapter
//            holder.tabLayout.setupWithViewPager(holder.viewPager, true)
//
//        } else {
        holder.rlInstaGramView.visibility = GONE
//        }

        if (!TextUtils.isEmpty(spot.profileOfUser.name)) {
            holder.name.text = spot.profileOfUser.name
        }
        if (!TextUtils.isEmpty(spot.profileOfUser.dob)) {
            holder.tvAge.text = ", " + CommonUtils.getAge(spot.profileOfUser.dob)
        }

        if (!TextUtils.isEmpty(spot.profileOfUser.distance.toString())) {
            holder.distance.text = spot.profileOfUser.distance.toString().split(".")[0] + " miles away"
        }
        if (!TextUtils.isEmpty(spot.profileOfUser.city)) {
            holder.tvLivesIn.text = spot.profileOfUser.city
        } else
            holder.cvLive.visibility = GONE

        if (!TextUtils.isEmpty(spot.profileOfUser.question1)) {
            holder.tvQuestion1.text = spot.profileOfUser.question1
            holder.tvAnswer1.text = spot.profileOfUser.answer1
        } else
            holder.cvQuestion1.visibility = GONE

        if (!TextUtils.isEmpty(spot.profileOfUser.question2)) {
            holder.tvQuestion2.text = spot.profileOfUser.question2
            holder.tvAnswer2.text = spot.profileOfUser.answer2
        } else
            holder.cvQuestion2.visibility = GONE

        if (!TextUtils.isEmpty(spot.profileOfUser.question3)) {
            holder.tvQuestion3.text = spot.profileOfUser.question3
            holder.tvAnswer3.text = spot.profileOfUser.answer3
        } else
            holder.cvQuestion3.visibility = GONE

        if (!TextUtils.isEmpty(spot.getisLinkedinUser()) && spot.getisLinkedinUser() != "No") {
            holder.llLinkedIn.visibility = VISIBLE
        } else {
            holder.llLinkedIn.visibility = GONE

        }

        if (spot.reactionForUser != null && spot.reactionForUser.size > 0) {
            for (i in 0 until spot.reactionForUser.size) {
                if (spot.reactionForUser[i].reaction.contains("superlike", ignoreCase = true)) {
                    holder.llSuperLike.visibility = VISIBLE
                }
            }
        } else {
            holder.llSuperLike.visibility = GONE
        }


        if (!TextUtils.isEmpty(spot.profileOfUser.occupation))
            holder.tvOccupation.text = spot.profileOfUser.occupation
        else
            holder.cvOccupation.visibility = GONE

        if (!TextUtils.isEmpty(spot.profileOfUser.school))
            holder.tvSchool.text = spot.profileOfUser.school
        else
            holder.cvSchool.visibility = GONE

        if (!TextUtils.isEmpty(spot.profileOfUser.aboutme)) {
            holder.cvAboutMe.visibility = VISIBLE
            holder.tvAboutMe.text = spot.profileOfUser.aboutme
        } else
            holder.cvAboutMe.visibility = GONE

        if (!TextUtils.isEmpty(spot.profileOfUser.ambitions)) {
            holder.cvInterest.visibility = VISIBLE
            holder.tvInterest.text = spot.profileOfUser.ambitions
        } else
            holder.cvInterest.visibility = GONE
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.alignItems = AlignItems.STRETCH
        holder.rvAbout.layoutManager = layoutManager
        holder.tagList.clear()
        if (!TextUtils.isEmpty(spot.profileOfUser.lookingFor)) {
            holder.tagList.add(DetailTagModel(spot.profileOfUser.lookingFor, R.drawable.ic_relation_white))
        }
        if (!TextUtils.isEmpty(spot.profileOfUser.height)) {
            var hetS = spot.profileOfUser.height
            if (hetS.contains(".")) {
                val het = hetS.toFloat()
                hetS = when {
                    het < 4 -> "< 4'0\""
                    het > 7 -> "> 7'0\""
                    else -> hetS.replace(".", "'") + "\""
                }
            }
            holder.tagList.add(DetailTagModel(hetS, R.drawable.ic_height))
            // holder.tagList.add(DetailTagModel(spot.profileOfUser.height, R.drawable.ic_height))
        }
        if (!TextUtils.isEmpty(spot.profileOfUser.zodiacSign)) {
            holder.tagList.add(DetailTagModel(spot.profileOfUser.zodiacSign, R.drawable.ic_zodiacsign))
        }
        if (!TextUtils.isEmpty(spot.profileOfUser.kids)) {
            holder.tagList.add(DetailTagModel(spot.profileOfUser.kids, R.drawable.ic_kids))
        }
        if (!TextUtils.isEmpty(spot.profileOfUser.relegion)) {
            holder.tagList.add(DetailTagModel(spot.profileOfUser.relegion, R.drawable.ic_religion))
        }
        if (!TextUtils.isEmpty(spot.profileOfUser.education)) {
            holder.tagList.add(DetailTagModel(spot.profileOfUser.education, R.drawable.ic_education))
        }
        if (!TextUtils.isEmpty(spot.profileOfUser.exercise)) {
            holder.tagList.add(DetailTagModel(spot.profileOfUser.exercise, R.drawable.ic_exercise))
        }
        if (!TextUtils.isEmpty(spot.profileOfUser.drink)) {
            holder.tagList.add(DetailTagModel(spot.profileOfUser.drink, R.drawable.ic_drink))
        }
        if (!TextUtils.isEmpty(spot.profileOfUser.smoke)) {
            holder.tagList.add(DetailTagModel(spot.profileOfUser.smoke, R.drawable.smoke_img))
        }
        if (!TextUtils.isEmpty(spot.profileOfUser.pets)) {
            holder.tagList.add(DetailTagModel(spot.profileOfUser.pets, R.drawable.dog_ic))
        }
        if (!TextUtils.isEmpty(spot.profileOfUser.political)) {
            holder.tagList.add(DetailTagModel(spot.profileOfUser.political, R.drawable.ic_political))
        }
        /*   val clipboard: ClipboardManager = context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
           val clip: ClipData = ClipData.newPlainText("List ", ""+holder.tagList)
           clipboard.setPrimaryClip(clip)*/
        holder.adapter = DetailTagAdapter(context, holder.tagList)
        holder.rvAbout.adapter = holder.adapter

        holder.image.setOnClickListener {
            val intent = Intent(context, ViewPagerActivity::class.java)
            intent.putExtra("images", Gson().toJson(spot.imageForUser.sortedWith(compareBy { it.orderId })))
            intent.putExtra("position", 0)
            context?.startActivity(intent)
        }
        holder.image2.setOnClickListener {
            val intent = Intent(context, ViewPagerActivity::class.java)
            intent.putExtra("images", Gson().toJson(spot.imageForUser.sortedWith(compareBy { it.orderId })))
            intent.putExtra("position", 1)
            context?.startActivity(intent)
        }
        holder.image3.setOnClickListener {
            val intent = Intent(context, ViewPagerActivity::class.java)
            intent.putExtra("images", Gson().toJson(spot.imageForUser.sortedWith(compareBy { it.orderId })))
            intent.putExtra("position", 2)
            context?.startActivity(intent)
        }
        holder.image4.setOnClickListener {
            val intent = Intent(context, ViewPagerActivity::class.java)
            intent.putExtra("images", Gson().toJson(spot.imageForUser.sortedWith(compareBy { it.orderId })))
            intent.putExtra("position", 3)
            context?.startActivity(intent)
        }
        holder.image5.setOnClickListener {
            val intent = Intent(context, ViewPagerActivity::class.java)
            intent.putExtra("images", Gson().toJson(spot.imageForUser.sortedWith(compareBy { it.orderId })))
            intent.putExtra("position", 4)
            context?.startActivity(intent)
        }
        holder.image6.setOnClickListener {
            val intent = Intent(context, ViewPagerActivity::class.java)
            intent.putExtra("images", Gson().toJson(spot.imageForUser.sortedWith(compareBy { it.orderId })))
            intent.putExtra("position", 5)
            context?.startActivity(intent)
        }

        holder.tvReport.setOnClickListener {
            listener.OnReportClick(spots[position].id)

        }

        baseActivity.hideLoading()
    }

    override fun getItemCount(): Int {
        return spots.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val tvAge: TextView = view.findViewById(R.id.tvage)
        var distance: TextView = view.findViewById(R.id.tv_distance)
        var tvOccupation: TextView = view.findViewById(R.id.tvOccupation)
        val ivVerifyLogo: ImageView = view.findViewById(R.id.ivVerifyLogo)
        val image: SimpleDraweeView = view.findViewById(R.id.item_image)
        val image2: SimpleDraweeView = view.findViewById(R.id.ivUserImage2)
        val image3: SimpleDraweeView = view.findViewById(R.id.ivUserImage3)
        val image4: SimpleDraweeView = view.findViewById(R.id.ivUserImage4)
        val image5: SimpleDraweeView = view.findViewById(R.id.ivUserImage5)
        val image6: SimpleDraweeView = view.findViewById(R.id.ivUserImage6)
        val rvAbout: RecyclerView = view.findViewById(R.id.rv_about)
        val cvLive: CardView = view.findViewById(R.id.cv_live)
        val tvAboutMe: TextView = view.findViewById(R.id.tv_about_me)
        val llLinkedIn: LinearLayout = view.findViewById(R.id.llLinkedin)
        val llSuperLike: LinearLayout = view.findViewById(R.id.ll_superlike)
        val cvAboutMe: CardView = view.findViewById(R.id.cv_about_me)
        val cvInterest: CardView = view.findViewById(R.id.cv_interest)
        val cvQuestion3: CardView = view.findViewById(R.id.cvQuestion3)
        val cvQuestion2: CardView = view.findViewById(R.id.cvQuestion2)
        val cvQuestion1: CardView = view.findViewById(R.id.cvQuestion1)
        val tvQuestion1: TextView = view.findViewById(R.id.tvQuestion1)
        val tvQuestion2: TextView = view.findViewById(R.id.tvQuestion2)
        val tvQuestion3: TextView = view.findViewById(R.id.tvQuestion3)
        val tvAnswer1: TextView = view.findViewById(R.id.tvAnswer1)
        val tvAnswer2: TextView = view.findViewById(R.id.tvAnswer2)
        val tvAnswer3: TextView = view.findViewById(R.id.tvAnswer3)
        val viewPager: ViewPager = view.findViewById(R.id.view_pager)
        val tvSchool: TextView = view.findViewById(R.id.tvSchool)
        val tvInterest: TextView = view.findViewById(R.id.tv_interest)
        val cvSchool: CardView = view.findViewById(R.id.cvSchool)
        val cvOccupation: CardView = view.findViewById(R.id.cv_occupation)
        val tvReport: TextView = view.findViewById(R.id.tv_report)
        val tvLivesIn: TextView = view.findViewById(R.id.tv_lives_in)
        val rlInstaGramView: RelativeLayout = view.findViewById(R.id.instaView)
        var adapter: DetailTagAdapter? = null
        var customPagerAdapter: CustomPagerAdapter? = null
        var tagList: MutableList<DetailTagModel> = ArrayList()
        var imageUser: List<ImageForUser> = ArrayList()
        val tabLayout: TabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

    }


}

