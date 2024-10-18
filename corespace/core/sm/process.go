package sm

import (
	C "core/constant"
	"core/log"
	"core/model"
	"os"
	"path"
	"regexp"
	"strconv"
)

func Process(request model.Request) (response model.Response, err error) {

	defer func() {
		if r := recover(); r != nil {
			if res, ok := r.(model.Response); ok {
				response = res
				err = nil
			}
		}
	}()

	flag := matchRoute(request.Url)
	log.Debugln("process url: %s, flag: %d", request.Url, flag)
	log.Debugln("process request: %v", request)

	content, err := os.ReadFile(path.Join(C.Path.BackendDir(), "sub-store-"+strconv.Itoa(flag)+".min.js"))
	if err != nil {
		log.Debugln("Failed to read file: %v", err)
		return model.Response{}, err
	}

	vm, err := PrepareJavascriptVM()
	if err != nil {
		log.Debugln("Failed to prepare javascript vm: %v", err)
		return model.Response{}, err
	}

	vm.Set("$request", request)

	log.Debugln("start to run scripts")

	_, err = vm.RunString(string(content))
	if err != nil {
		log.Debugln("Failed to run scripts: %v", err)
	}

	return model.Response{}, nil
}

func matchRoute(url string) (flag int) {
	regex1 := regexp.MustCompile("^https?://sub\\.store/((download)|api/(preview|sync|(utils/node-info)))")
	regex0 := regexp.MustCompile("^https?://sub\\.store")

	if regex1.MatchString(url) {
		flag = 1
	} else if regex0.MatchString(url) {
		flag = 0
	} else {
		flag = -1
	}

	return flag
}
