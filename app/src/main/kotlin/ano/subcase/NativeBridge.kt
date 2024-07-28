package ano.subcase

object NativeBridge {

    init {
        System.loadLibrary("bridge")
        nativeInit(caseApp.filesDir.absolutePath)
    }

    private external fun nativeInit(home: String)

    external fun nativeStartBackend(port: Int, allowLan: Boolean)
    external fun nativeStopBackend()

    external fun nativeStartFrontend(port: Int, allowLan: Boolean)
    external fun nativeStopFrontend()

    external fun nativeForceGc()
}