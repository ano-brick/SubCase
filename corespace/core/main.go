package main

import (
	"core/constant"
	"core/log"
	"core/model"
	"core/sm"
	"os"
	"path"
)

func main() {
	// set home directory
	wd, err := os.Getwd()
	if err != nil {
		panic(err)
	}
	constant.SetHomeDir(path.Join(wd, "temp"))

	request := model.Request{
		Url:     "https://sub.store/api/subs",
		Method:  "GET",
		Headers: map[string]string{},
		Body:    "",
	}

	response, err := sm.Process(request)
	if err != nil {
		log.Debugln("Failed to process request: %v", err)
		return
	}

	log.Debugln("Response: %v", response)

}
