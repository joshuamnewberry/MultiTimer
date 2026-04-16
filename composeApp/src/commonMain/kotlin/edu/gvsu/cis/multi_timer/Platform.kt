package edu.gvsu.cis.multi_timer

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform