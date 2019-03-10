/*
 *  Copyright 2019 Dmitriy Ponomarenko
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for
 *  additional information regarding copyright ownership. The ASF licenses this
 *  file to you under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

package com.dimowner.elections.app.poll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dimowner.elections.R
import com.dimowner.elections.data.model.Candidate
import com.dimowner.elections.util.AndroidUtils

private const val VIEW_TYPE_NORMAL = 1
private const val VIEW_TYPE_HEADER = 2
private const val VIEW_TYPE_FOOTER = 3

class PollsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	private var data: MutableList<Candidate> = ArrayList()

	private var itemClickListener: ItemClickListener? = null

	var selectedItem = -1

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		when (viewType) {
			VIEW_TYPE_HEADER -> {
				val view = View(parent.context)
//				val height: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//					AndroidUtils.getStatusBarHeight(viewGroup.context) + viewGroup.context.resources.getDimension(R.dimen.toolbar_height).toInt()
//				} else {
//					viewGroup.context.resources.getDimension(R.dimen.toolbar_height).toInt()
//				}
				val height: Int = parent.context.resources.getDimension(R.dimen.toolbar_height).toInt()
				val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
				view.layoutParams = lp
				return UniversalViewHolder(view)
			}
			VIEW_TYPE_FOOTER -> {
				val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_footer, parent, false)
			 	return FooterViewHolder(v)
			}
			else -> {
				val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_poll, parent, false)
				return ItemViewHolder(v)
			}
		}
	}

	override fun onBindViewHolder(h: RecyclerView.ViewHolder, position: Int) {
		if (h.itemViewType == VIEW_TYPE_NORMAL) {
			val pos = h.adapterPosition-1
			val holder = h as ItemViewHolder
			holder.name.text = data[pos].firstName + " " + data[pos].surName
			holder.description.text = data[pos].party
			holder.image.setImageResource(AndroidUtils.candidateCodeToResource(data[pos].iconId))

			if (pos == selectedItem) {
				holder.itemPanel.setBackgroundResource(R.drawable.ripple_yellow)
				holder.name.setTextColor(holder.name.context.resources.getColor(R.color.main_blue_dark))
				holder.description.setTextColor(holder.name.context.resources.getColor(R.color.main_blue_dark))
			} else {
				holder.itemPanel.setBackgroundResource(R.drawable.ripple_blue)
				holder.name.setTextColor(holder.name.context.resources.getColor(R.color.text_primary_light))
				holder.description.setTextColor(holder.name.context.resources.getColor(R.color.text_secondary_light))
			}

			holder.itemPanel.setOnClickListener { v ->
				run {
					setActiveItem(pos)
					itemClickListener?.onItemClick(v, pos, selectedItem != -1)
				}
			}
		} else {
			//Do nothing
		}
	}

	private fun setActiveItem(activeItem: Int) {
		if (this.selectedItem == activeItem) {
			this.selectedItem = -1
			notifyItemChanged(activeItem+1)
		} else {
			val prev = this.selectedItem
			this.selectedItem = activeItem
			notifyItemChanged(prev+1)
			notifyItemChanged(activeItem+1)
		}
	}

	public fun getSelectedItem(): Candidate? {
		if (selectedItem >= 0 && selectedItem < data.size) {
			return data[selectedItem]
		} else {
			return null
		}
	}

	override fun getItemViewType(position: Int): Int {
		return if (position == 0) {
			VIEW_TYPE_HEADER
		} else if (position >= data.size+1) {
			VIEW_TYPE_FOOTER
		} else {
			VIEW_TYPE_NORMAL
		}
	}

	override fun getItemCount(): Int {
		return data.size + 2
	}

	fun setData(list: List<Candidate>) {
		if (data.isEmpty()) {
			this.data.addAll(list)
			notifyDataSetChanged()
		} else {
			val diff = CandDiffUtilCallback(data, list)
			val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(diff)
			this.data.clear()
			this.data.addAll(list)
			diffResult.dispatchUpdatesTo(this)
		}
	}

	fun setItemClickListener(itemClickListener: ItemClickListener) {
		this.itemClickListener = itemClickListener
	}

	internal inner class ItemViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
		var name: TextView = view.findViewById(R.id.list_item_name)
		var description: TextView = view.findViewById(R.id.list_item_description)
		var image: ImageView = view.findViewById(R.id.list_item_image)
		var txtVal: TextView = view.findViewById(R.id.list_item_value)
		var itemPanel: LinearLayout = view.findViewById(R.id.item_panel)
	}

	internal inner class FooterViewHolder internal constructor(internal val view: View) : RecyclerView.ViewHolder(view)

	inner class UniversalViewHolder(internal var view: View) : RecyclerView.ViewHolder(view)

	interface ItemClickListener {
		fun onItemClick(view: View, position: Int, selected: Boolean)
	}
}