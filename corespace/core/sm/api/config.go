package api

import "github.com/dop251/goja"

func RegisterConfigAPI(vm *goja.Runtime) error {
	if err := vm.Set("$config", 2); err != nil {
		return err
	}

	return nil
}
