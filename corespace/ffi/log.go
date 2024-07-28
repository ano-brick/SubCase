package main

//#include "bridge.h"
import "C"

import "core/log"

func init() {
	go func() {
		sub := log.Subscribe()
		defer log.UnSubscribe(sub)

		for item := range sub {
			msg := item.(log.Event)

			cPayload := C.CString(msg.Payload)

			switch msg.LogLevel {
			case log.ERROR:
				C.log_error(cPayload)
			case log.WARNING:
				C.log_warn(cPayload)
			case log.DEBUG:
				C.log_debug(cPayload)
			case log.INFO:
				C.log_info(cPayload)
			case log.SILENT:
				C.log_verbose(cPayload)
			}
		}

	}()
}
