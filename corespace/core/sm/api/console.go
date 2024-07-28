package api

import (
	"core/log"
	"github.com/dop251/goja"
)

func RegisterConsoleAPI(vm *goja.Runtime) error {
	consoleLog := func(call goja.FunctionCall) goja.Value {
		log.Debugln("[Console] %s", call.Arguments[0].String())
		return goja.Undefined()
	}

	err := vm.Set("console", map[string]func(goja.FunctionCall) goja.Value{
		"log": consoleLog,
	})

	if err != nil {
		return err
	}

	return nil
}
