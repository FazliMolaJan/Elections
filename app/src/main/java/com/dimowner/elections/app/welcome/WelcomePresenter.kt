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

package com.dimowner.elections.app.welcome

import android.content.Context
import android.os.Bundle
import com.dimowner.elections.data.Prefs
import com.dimowner.elections.places.PlacesProvider
import com.google.android.gms.common.api.GoogleApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

open class WelcomePresenter(
		private val prefs: Prefs,
		private val context: Context,
		private val placesProvider: PlacesProvider) : WelcomeContract.UserActionsListener {

	private var view: WelcomeContract.View? = null

	private val compositeDisposable: CompositeDisposable = CompositeDisposable()


	override fun bindView(view: WelcomeContract.View) {
		this.view = view
		placesProvider.connect(
				object : GoogleApiClient.ConnectionCallbacks {
					override fun onConnected(bundle: Bundle?) {
						Timber.v("onConnected")
					}

					override fun onConnectionSuspended(i: Int) {
						Timber.v("onConnectionSuspended")
					}
				}
		)
	}

	override fun unbindView() {
		this.view = null
		compositeDisposable.clear()
		placesProvider.disconnect()
	}

	override fun firstRunExecuted() {
		prefs.setFirstRunExecuted()
	}

	override fun locate(context: Context) {
		compositeDisposable.add(
				placesProvider.findCurrentLocation()
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe({
							location -> Timber.v("Location = $location")
							if (!location.countryCode.isBlank()) {
								prefs.setCountryCode(location.countryCode)
								prefs.setCountryName(location.countryName)
								prefs.setCity(location.city)
								view?.startPollActivity()
							} else {
								view?.showError("Failed to find location")
							}
						}, { Timber.e(it) }))
	}
}
