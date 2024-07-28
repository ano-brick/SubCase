package api

import (
	"core/component/cache"
	"core/log"
	"core/util"
	"github.com/dop251/goja"
)

func RegisterPersistentStoreAPI(vm *goja.Runtime) error {
	persistentStoreWrite := func(call goja.FunctionCall) goja.Value {
		value := call.Arguments[0].String()
		key := call.Arguments[1].String()

		log.Debugln("[SM][persistentStore][Write] key = %s , value = %s  ", key, util.RemoveSpace(value))

		if err := cache.Cache().WritePersistentStore([]byte(key), []byte(value)); err != nil {
			log.Debugln("Failed to write to store: %v", err)
		}

		return goja.Undefined()
	}

	persistentStoreRead := func(call goja.FunctionCall) goja.Value {
		key := call.Arguments[0].String()

		if res := cache.Cache().ReadPersistentStore([]byte(key)); res != nil {
			log.Debugln("[SM][persistentStore][Read] key = %s , value = %s  ", key, util.RemoveSpace(string(res)))
			return vm.ToValue(string(res))
		}

		return goja.Undefined()
	}

	err := vm.Set("$persistentStore", map[string]func(goja.FunctionCall) goja.Value{
		"write": persistentStoreWrite,
		"read":  persistentStoreRead,
	})
	if err != nil {
		return err
	}

	return nil
}
