package main

import (
	"net/http"
	"testing"
)

func Test_http(t *testing.T) {
	//Request = { 0 map[User-Agent:clash.meta]
	req, err := http.NewRequest("GET", "https://wd-red.com/subscribe/pjnqpn-0kdsqryd", nil)
	if err != nil {
		t.Fatal(err)
	}
	req.Header.Set("User-Agent", "clash.meta")
	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		t.Fatal(err)
	}
	t.Log(resp)

}

func Test_httpserver(t *testing.T) {
	//backend.Httpserver()
}
