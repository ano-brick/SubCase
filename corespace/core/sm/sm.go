package sm

import (
	"core/sm/api"
	"github.com/dop251/goja"
)

func PrepareJavascriptVM() (*goja.Runtime, error) {
	vm := goja.New()

	vm.SetFieldNameMapper(goja.TagFieldNameMapper("json", true))

	if err := registerAPIs(vm); err != nil {
		return nil, err
	}

	return vm, nil
}

func registerAPIs(vm *goja.Runtime) error {
	if err := api.RegisterConsoleAPI(vm); err != nil {
		return err
	}

	if err := api.RegisterLoonAPI(vm); err != nil {
		return err
	}

	if err := api.RegisterPersistentStoreAPI(vm); err != nil {
		return err
	}

	if err := api.RegisterScriptAPI(vm); err != nil {
		return err
	}

	if err := api.RegisterHttpClientAPI(vm); err != nil {
		return err
	}

	if err := api.RegisterEnvironmentAPI(vm); err != nil {
		return err
	}

	if err := api.RegisterConfigAPI(vm); err != nil {
		return err
	}

	if err := api.RegisterDoneAPI(vm); err != nil {
		return err
	}

	return nil
}
