package backend

import (
	"context"
	"core/log"
	"core/model"
	"core/sm"
	"io"
	"net/http"
	"strconv"
	"time"

	"github.com/go-chi/chi"
)

var backendServer *http.Server

type BackendHandler struct{}

func (h *BackendHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {

	url := "https://sub.store" + r.RequestURI

	// read request headers

	headers := make(map[string]string)

	for name, values := range r.Header {
		v := ""
		for _, value := range values {
			v += value
		}
		headers[name] = v
	}

	body := ""

	if r.Method == "POST" || r.Method == "PATCH" || r.Method == "PUT" {
		bodyBytes, _ := io.ReadAll(r.Body)
		body = string(bodyBytes)
	}

	req := model.Request{
		Url:     url,
		Method:  r.Method,
		Headers: headers,
		Body:    body,
	}

	// process request
	resp, err := sm.Process(req)
	if err != nil {
		log.Debugln("Failed to process request: %v", err)
		return
	}

	// write response headers
	for name, value := range resp.Headers {
		w.Header().Set(name, value)
	}

	w.Write([]byte(resp.Body))
}

func Httpserver(port int, allowLan bool) error {
	handler := &BackendHandler{}

	r := chi.NewRouter()

	r.Handle("/*", handler)

	ip := ""

	if allowLan {
		ip = "0.0.0.0"
	} else {
		ip = "127.0.0.1"
	}

	backendServer = &http.Server{
		Addr:    ip + ":" + strconv.Itoa(port),
		Handler: r,
	}

	err := backendServer.ListenAndServe()
	if err != nil {
		return err
	}

	return nil
}

func Start(port int, allowLan bool) {
	err := Httpserver(port, allowLan)
	if err != nil {
		log.Debugln("Failed to start http server: %v", err)
	}
}

func Stop() {
	if backendServer != nil {
		ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
		defer cancel()

		if err := backendServer.Shutdown(ctx); err != nil {
			log.Debugln("Failed to stop http server: %v", err)
		}
	}
}
