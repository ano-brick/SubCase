package model

type Response struct {
	Status  int64             `json:"status"`
	Headers map[string]string `json:"headers"`
	Body    string            `json:"body"`
}
