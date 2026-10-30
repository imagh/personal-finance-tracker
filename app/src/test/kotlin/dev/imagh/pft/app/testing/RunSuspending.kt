package dev.imagh.pft.app.testing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

/**
 * Runs a suspend test body to completion on the calling thread. Returns `Unit` whatever the body
 * ends with, which JUnit 4 requires of a test method (`fun test() = runSuspending { ... }`).
 *
 * Plain `runBlocking`, not `runTest`: Room does its work on its own executors in real time, so
 * virtual time would only hide waits.
 */
fun runSuspending(block: suspend CoroutineScope.() -> Unit) {
    runBlocking { block() }
}
