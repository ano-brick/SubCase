package api

import (
	"core/log"
	"crypto/tls"
	"encoding/json"
	"github.com/dop251/goja"
	"io"
	"net/http"
	"strings"
	"time"
)

type RequestParams struct {
	Url          string            `json:"url"`
	Timeout      int               `json:"timeout"`
	Headers      map[string]string `json:"headers"`
	Body         string            `json:"body"`
	IsBodyBase64 bool              `json:"body-base64"`
	Node         string            `json:"node"`
	BinaryMode   bool              `json:"binary"`
	AutoRedirect bool              `json:"auto-redirect"`
	AutoCookie   bool              `json:"auto-cookie"`
}

var scriptMachine = goja.New()

func RegisterHttpClientAPI(vm *goja.Runtime) error {

	scriptMachine = vm

	if err := vm.Set("$httpClient", map[string]func(goja.FunctionCall) goja.Value{
		"get":     httpGet,
		"post":    httpPost,
		"head":    httpHead,
		"delete":  httpDelete,
		"put":     httpPut,
		"options": httpOptions,
		"patch":   httpPatch,
	}); err != nil {
		return err
	}

	return nil
}

// httpGet
// do a get request, params is a map ,other is a callback function
func httpGet(call goja.FunctionCall) goja.Value {
	return doRequest("GET", call)
}

func httpPost(call goja.FunctionCall) goja.Value {
	return doRequest("HEAD", call)
}

func httpHead(call goja.FunctionCall) goja.Value {
	return doRequest("HEAD", call)
}

func httpDelete(call goja.FunctionCall) goja.Value {
	return doRequest("DELETE", call)
}

func httpPut(call goja.FunctionCall) goja.Value {
	return doRequest("PUT", call)
}

func httpOptions(call goja.FunctionCall) goja.Value {
	return doRequest("OPTIONS", call)
}

func httpPatch(call goja.FunctionCall) goja.Value {
	return doRequest("PATH", call)
}

func doRequest(method string, call goja.FunctionCall) goja.Value {
	request := call.Arguments[0].Export()
	callback, ok := goja.AssertFunction(call.Arguments[1])

	requestObj := RequestParams{
		Timeout: 5000,
	}
	jsonBytes, err := json.Marshal(request)
	log.Debugln("[HttpClient][%s] JSON  = %v", method, string(jsonBytes))

	err = json.Unmarshal(jsonBytes, &requestObj)
	if err != nil {
		log.Debugln("[HttpClient][%s] Failed to marshal request: %v", method, err)
		return goja.Undefined()
	}

	log.Debugln("[HttpClient][%s] Request = %v", method, requestObj)

	// 创建一个新的HTTP请求
	req, err := http.NewRequest(method, requestObj.Url, strings.NewReader(requestObj.Body))
	if err != nil {
		log.Debugln("[HttpClient][%s] Failed to create request: %v", method, err)
		return goja.Undefined()
	}

	// 设置HTTP头部
	for key, value := range requestObj.Headers {
		req.Header.Set(key, value)
	}

	// 发送HTTP请求
	client := &http.Client{
		Timeout: time.Duration(requestObj.Timeout) * time.Millisecond,
		Transport: &http.Transport{
			// force use http/1.1
			TLSNextProto: make(map[string]func(authority string, c *tls.Conn) http.RoundTripper),
		},
	}

	resp, err := client.Do(req)
	if err != nil {
		log.Debugln("[HttpClient][%s] Failed to send request: %v", method, err)
		_, err = callback(nil, scriptMachine.ToValue(err.Error()), goja.Undefined(), goja.Undefined())
		if err != nil {
			log.Debugln("[HttpClient][%s] Failed to call function: %v", method, err)
			return goja.Undefined()
		}
		return goja.Undefined()
	}
	defer resp.Body.Close()

	// 读取响应体
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		log.Debugln("[HttpClient][%s] Failed to read response body: %v", method, err)
		return goja.Undefined()
	}

	bodyStr := string(body)

	log.Debugln("[HttpClient][%s][Response] %s", method, resp)
	log.Debugln("[HttpClient][%s][Response] body : %s", method, bodyStr)

	if !ok {
		log.Debugln("[HttpClient][%s] Argument is not a function", method)
		return goja.Undefined()
	}

	errorMsg := goja.Undefined()
	data := scriptMachine.ToValue(bodyStr)

	responseObj := scriptMachine.NewObject()
	err = responseObj.Set("status", resp.StatusCode)
	if err != nil {
		log.Debugln("[HttpClient][%s] Failed to set status: %v", method, err)
		return nil
	}

	headersObj := scriptMachine.NewObject()
	for key, value := range resp.Header {
		err := headersObj.Set(key, value[0])
		if err != nil {
			log.Debugln("[HttpClient][s] Failed to set header: %v", method, err)
			return goja.Undefined()
		}
	}

	err = responseObj.Set("headers", headersObj)
	if err != nil {
		log.Debugln("[HttpClient][%s] Failed to set headers: %v", method, err)
		return goja.Undefined()
	}

	log.Debugln("[HttpClient][%s][responseObj] %v", method, responseObj.Export())

	_, err = callback(nil, errorMsg, responseObj, data)
	if err != nil {
		log.Debugln("[HttpClient][%s] Failed to call function: %v", method, err)
		return goja.Undefined()
	}

	return goja.Undefined()
}
