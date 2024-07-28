package util

import "strings"

func RemoveSpace(s string) string {
	newStr := strings.ReplaceAll(s, "\n", "")
	newStr = strings.ReplaceAll(newStr, "\r", "")
	newStr = strings.ReplaceAll(newStr, "\t", "")
	newStr = strings.ReplaceAll(newStr, " ", "")
	return newStr
}
