package com.catinthedark.squatality.server

const val eventName = "message"
const val headerClientID = "clientID"
const val tickRate = 50f
const val tickDelay = (1000f / tickRate).toLong() // in milliseconds
