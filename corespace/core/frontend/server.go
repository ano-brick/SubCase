package frontend

import (
	"context"
	C "core/constant"
	"core/log"
	"net/http"
	"strconv"
	"time"
)

var frontendServer *http.Server

func Start(port int, allowLan bool) error {

	ip := ""

	if allowLan {
		ip = "0.0.0.0"
	} else {
		ip = "127.0.0.1"
	}

	if frontendServer == nil {
		http.Handle("/", http.FileServer(http.Dir(C.Path.FrontendDir())))
	}

	frontendServer = &http.Server{
		Addr: ip + ":" + strconv.Itoa(port),
	}

	err := frontendServer.ListenAndServe()
	if err != nil {
		return err
	}

	return nil
}

func Stop() {
	if frontendServer != nil {
		ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
		defer cancel()

		if err := frontendServer.Shutdown(ctx); err != nil {
			log.Debugln("Failed to stop Frontend server: %v", err)
		}
	}
}
