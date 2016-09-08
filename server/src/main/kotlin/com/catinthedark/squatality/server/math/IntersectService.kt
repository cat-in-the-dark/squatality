package com.catinthedark.squatality.server.math

import com.catinthedark.Const

class IntersectService {
    fun topWallPenetration(y: Float, radius: Float): Float {
        return Math.max(0f, Const.UI.verticalBorderWidth - (y - radius))
    }

    fun bottomWallPenetration(y: Float, radius: Float): Float {
        return Math.max(0f, y + radius - (Const.UI.verticalBorderWidth + Const.UI.fieldHeight))
    }

    fun leftWallPenetration(x: Float, radius: Float): Float {
        return Math.max(0f, Const.UI.horizontalBorderWidth - (x - radius))
    }

    fun rightWallPenetration(x: Float, radius: Float): Float {
        return Math.max(0f, x + radius - (Const.UI.horizontalBorderWidth + Const.UI.fieldWidth))
    }

    fun intersectWalls(x: Float, y: Float, radius: Float): Boolean {
        return (topWallPenetration(y, radius) > 0
            || bottomWallPenetration(y, radius) > 0
            || leftWallPenetration(x, radius) > 0
            || rightWallPenetration(x, radius) > 0)
    }
}
