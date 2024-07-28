package api

import (
	"core/log"
	"core/model"
	"fmt"
	"github.com/dop251/goja"
)

func RegisterDoneAPI(vm *goja.Runtime) error {
	// $done({ status:200, headers:{}, body:"" }) 、 $done({}) 、$done()
	doneFunc := func(call goja.FunctionCall) goja.Value {
		if len(call.Arguments) != 0 {
			responseValue := call.Arguments[0].Export()
			log.Debugln("[DoneAPI] %v", responseValue)

			var response model.Response

			// TODO 使用JSON优化

			// 检查 responseValue 是否是一个包含 "response" 字段的 map
			if responseMap, ok := responseValue.(map[string]interface{}); ok {
				if innerResponse, ok := responseMap["response"]; ok {
					responseValue = innerResponse
					if responseMap, ok = responseValue.(map[string]interface{}); ok {
						if status, ok := responseMap["status"]; ok {
							response.Status = status.(int64)
						}
						if headers, ok := responseMap["headers"]; ok {
							headersMap := headers.(map[string]interface{})
							response.Headers = make(map[string]string)

							for key, value := range headersMap {
								response.Headers[key] = fmt.Sprintf("%v", value)
							}

							if len(response.Headers) == 0 {
								response.Headers["Content-Type"] = "application/json"
							}

						}
						if body, ok := responseMap["body"]; ok {
							response.Body = body.(string)
						}
					}
				}
			}

			panic(response)
		}

		return goja.Undefined()
	}

	if err := vm.Set("$done", doneFunc); err != nil {
		return err
	}

	return nil
}
