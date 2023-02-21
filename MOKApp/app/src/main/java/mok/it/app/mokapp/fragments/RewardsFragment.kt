package mok.it.app.mokapp.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_phonebook_item.view.*
import kotlinx.android.synthetic.main.card_reward.*
import kotlinx.android.synthetic.main.card_reward.view.*
import kotlinx.android.synthetic.main.fragment_rewards.*
import mok.it.app.mokapp.R
import mok.it.app.mokapp.baseclasses.BaseFireFragment
import mok.it.app.mokapp.firebase.FirebaseUserObject
import mok.it.app.mokapp.firebase.FirebaseUserObject.userModel
import mok.it.app.mokapp.model.Reward
import mok.it.app.mokapp.model.User
import mok.it.app.mokapp.recyclerview.PhoneBookViewHolder
import mok.it.app.mokapp.recyclerview.RewardViewHolder
import mok.it.app.mokapp.recyclerview.WrapContentLinearLayoutManager

class RewardsFragment : BaseFireFragment() {
    lateinit var adapter: FirestoreRecyclerAdapter<*, *>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rewards, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.stopListening()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAdapter()
        updateUI()
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            WrapContentLinearLayoutManager(this.context)
    }

    private fun initializeAdapter() {
        val options: FirestoreRecyclerOptions<Reward?> = FirestoreRecyclerOptions.Builder<Reward>()
            .setQuery(
                firestore.collection(rewardCollectionPath).orderBy("price", Query.Direction.ASCENDING),
                Reward::class.java
            )
            .build()

        adapter =
            object : FirestoreRecyclerAdapter<Reward?, RewardViewHolder?>(options) {
                var context: Context? = null
                override fun onBindViewHolder(
                    holder: RewardViewHolder,
                    position: Int,
                    model: Reward
                ) {
                    val ivImg: ImageView = holder.itemView.findViewById(R.id.rewardImage)
                    loadImage(ivImg, model.icon)
                    holder.itemView.rewardName.text = model.name
                    holder.itemView.rewardPrice.text = model.price.toString()
                    if (userModel.points >= model.price) {
                        holder.itemView.requestButton.visibility = View.VISIBLE
                    }
                    if (userModel.requestedRewards.contains(model.documentId)) {
                        holder.itemView.requestButton.visibility = View.GONE
                        holder.itemView.achievedText.visibility = View.VISIBLE
                    }
                }

                override fun onCreateViewHolder(group: ViewGroup, i: Int): RewardViewHolder {
                    val view: View = LayoutInflater.from(group.context)
                        .inflate(R.layout.card_reward, group, false)
                    return RewardViewHolder(view)
                }

                override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                    super.onAttachedToRecyclerView(recyclerView)
                    context = recyclerView.context
                }

                fun loadImage(imageView: ImageView, imageURL: String): Boolean {
                    return try {
                        Picasso.get().apply {
                            load(imageURL).into(imageView)
                        }
                        true
                    } catch (e: Exception) {
                        false
                    }
                }
            }
    }

    private fun updateUI(){
        pointsText.text = getString(R.string.my_points) + " " + userModel.points
    }
}