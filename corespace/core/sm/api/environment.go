package api

import "github.com/dop251/goja"

func RegisterEnvironmentAPI(vm *goja.Runtime) error {
	if err := vm.Set("$environment", 3); err != nil {
		return err
	}

	return nil
}
