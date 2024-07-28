package main

//#include "bridge.h"
import "C"

import (
	"core/backend"
	"core/constant"
	"core/frontend"
	"core/log"
	"runtime"
)

//export coreInit
func coreInit(homeDir *C.char) {
	home := C.GoString(homeDir)
	constant.SetHomeDir(home)

	log.Debugln("Home directory: %s", home)
}

//export coreStartBackend
func coreStartBackend(port int, allowLan bool) {
	log.Debugln("Starting backend , port: %d , allowLan: %t ", port, allowLan)
	backend.Start(port, allowLan)
}

//export coreStopBackend
func coreStopBackend() {
	log.Debugln("Stopping backend...")
	backend.Stop()
}

//export coreStartFrontend
func coreStartFrontend(port int, allowLan bool) {
	log.Debugln("Starting frontend , port: %d , allowLan: %t ", port, allowLan)
	err := frontend.Start(port, allowLan)
	if err != nil {
		log.Debugln("Failed to start frontend: %v", err)
	}
}

//export coreStopFrontend
func coreStopFrontend() {
	log.Debugln("Stopping frontend...")
	frontend.Stop()
}

//export coreForceGC
func coreForceGC() {
	go func() {
		log.Debugln("doing force GC")

		runtime.GC()
	}()
}
