package constant

import (
	P "path"
)

var Path = path{homeDir: ""}

type path struct {
	homeDir string
}

// SetHomeDir is used to set the configuration path
func SetHomeDir(root string) {
	Path.homeDir = root
}

func (p *path) Cache() string {
	return P.Join(p.homeDir, "cache.db")
}

func (p *path) BackendDir() string {
	return P.Join(p.homeDir, "backend")
}

func (p *path) FrontendDir() string {
	return P.Join(p.homeDir, "frontend")
}
