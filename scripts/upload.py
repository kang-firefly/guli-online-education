#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""上传文件到 guli 服务器"""
import os, sys, time, paramiko

HOST, USER = '122.51.6.69', 'ubuntu'
KEY = os.path.expanduser('~/.ssh/guli_deploy')


def upload(local, remote):
    if hasattr(sys.stdout, 'reconfigure'):
        sys.stdout.reconfigure(encoding='utf-8', errors='replace')
    size = os.path.getsize(local)
    c = paramiko.SSHClient()
    c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    c.connect(HOST, 22, username=USER, key_filename=KEY,
              timeout=20, banner_timeout=20, auth_timeout=20,
              look_for_keys=False, allow_agent=False)
    sftp = c.open_sftp()
    start = time.time()
    last = [0]

    def cb(done, total):
        pct = done * 100 // total
        if pct >= last[0] + 10:
            last[0] = pct
            speed = done / 1024 / 1024 / max(time.time() - start, 0.1)
            print(f'  {pct:3d}%  {done/1024/1024:.1f}/{size/1024/1024:.1f} MB  {speed:.1f} MB/s', flush=True)

    sftp.put(local, remote, callback=cb)
    sftp.close()
    c.close()
    print(f'完成: {remote}  ({time.time()-start:.0f}s)')


if __name__ == '__main__':
    upload(sys.argv[1], sys.argv[2])
