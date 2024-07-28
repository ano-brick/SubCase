package api

import "github.com/dop251/goja"

func RegisterScriptAPI(vm *goja.Runtime) error {
	if err := vm.Set("$script", 5); err != nil {
		return err
	}

	return nil
}
