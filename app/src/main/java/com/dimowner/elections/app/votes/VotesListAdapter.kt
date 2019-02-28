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

package com.dimowner.elections.app.votes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dimowner.elections.R
import com.dimowner.elections.data.model.Vote

class VotesListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	private var data: List<Vote> = ArrayList()

	private var itemClickListener: ItemClickListener? = null

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
		return ItemViewHolder(v)
	}

	override fun onBindViewHolder(h: RecyclerView.ViewHolder, position: Int) {
		val pos = h.adapterPosition
		val holder = h as ItemViewHolder
		holder.name.text = data[pos].device + " " + data[pos].android
		holder.description.text = data[pos].candidateId.toString() + " age:" + data[pos].age
		holder.image.setImageResource(R.mipmap.ic_elections)

		holder.view.setOnClickListener { v -> itemClickListener?.onItemClick(v, pos) }
	}

	override fun getItemCount(): Int {
		return data.size
	}

	fun setData(data: List<Vote>) {
		this.data = data
		notifyDataSetChanged()
	}

	fun setItemClickListener(itemClickListener: ItemClickListener) {
		this.itemClickListener = itemClickListener
	}

	internal inner class ItemViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
		var name: TextView = view.findViewById(R.id.list_item_name)
		var description: TextView = view.findViewById(R.id.list_item_description)
		var image: ImageView = view.findViewById(R.id.list_item_image)
		var txtVal: TextView = view.findViewById(R.id.list_item_value)
	}

	interface ItemClickListener {
		fun onItemClick(view: View, position: Int)
	}
}