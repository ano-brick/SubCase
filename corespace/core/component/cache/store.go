package cache

import (
	C "core/constant"
	"core/log"
	"go.etcd.io/bbolt"
	"os"
	"sync"
	"time"
)

var (
	fileMode os.FileMode = 0o666

	bucketStore = []byte("store")
)

type CacheFile struct {
	DB *bbolt.DB
}

func (c *CacheFile) WritePersistentStore(key, value []byte) error {
	if c.DB == nil {
		return nil
	}

	err := c.DB.Batch(
		func(tx *bbolt.Tx) error {
			bucket, err := tx.CreateBucketIfNotExists(bucketStore)
			if err != nil {
				return err
			}
			return bucket.Put(key, value)
		})
	if err != nil {
		log.Debugln("Failed to write to store: %v", err)
	}

	return err
}

func (c *CacheFile) ReadPersistentStore(key []byte) []byte {
	if c.DB == nil {
		return nil
	}

	tx, err := c.DB.Begin(false)
	if err != nil {
		return nil
	}
	defer tx.Rollback()

	bucket := tx.Bucket(bucketStore)
	if bucket == nil {
		return nil
	}

	return bucket.Get(key)
}

func (c *CacheFile) Close() error {
	return c.DB.Close()
}

// Cache is a singleton instance of CacheFile
var Cache = sync.OnceValue(func() *CacheFile {
	options := bbolt.Options{Timeout: time.Second}
	db, err := bbolt.Open(C.Path.Cache(), fileMode, &options)

	switch err {
	case bbolt.ErrInvalid, bbolt.ErrChecksum, bbolt.ErrVersionMismatch:
		err = os.Remove("store.db")
		if err != nil {
			log.Debugln("Failed to remove store.db: %v", err)
			break
		}
		log.Debugln("Removed invalid store file and create new one")
		db, err = bbolt.Open("store.db", fileMode, &options)
	}

	if err != nil {
		log.Debugln("Failed to open store.db: %v", err)
	}

	return &CacheFile{
		DB: db,
	}
})
