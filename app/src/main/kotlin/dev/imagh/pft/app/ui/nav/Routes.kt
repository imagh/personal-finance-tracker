package dev.imagh.pft.app.ui.nav

/**
 * The navigation routes, as plain strings (plan decision D3): Navigation 2.8's type-safe routes need
 * the kotlinx-serialization compiler plugin, which the project does not use. Routes carry no
 * arguments yet, so a string constant is all there is to get wrong. The debug-only design catalogue
 * route lives in `src/debug` and is not listed here.
 */
object Routes {
    /** First-run setup: shown only while no identity exists; popped off the back stack on completion. */
    const val SETUP = "setup"

    /** Home: where every later launch starts, and where Back leaves the app. */
    const val HOME = "home"
}
