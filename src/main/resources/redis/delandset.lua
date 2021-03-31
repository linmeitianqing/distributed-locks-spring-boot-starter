redis.call('DEL', KEYS[1])
redis.call('SET', KEYS[1], ARGV[1])
redis.call('PEXPIRE', KEYS[1], ARGV[2])
return 1