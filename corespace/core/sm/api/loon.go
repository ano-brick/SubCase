package api

import "github.com/dop251/goja"

func RegisterLoonAPI(vm *goja.Runtime) error {
	if err := vm.Set("$loon", 0); err != nil {
		return err
	}

	return nil
}
