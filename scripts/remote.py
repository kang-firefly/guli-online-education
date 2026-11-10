#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""远程执行器：ssh 到 guli 服务器跑命令

用法:
    python remote.py "命令1" "命令2" ...
    python remote.py -f script.sh          # 跑本地脚本
    python remote.py --sudo "命令"          # 用 sudo（若免密）
"""
import sys, os, paramiko

HOST = '122.51.6.69'
USER = 'ubuntu'
KEY = os.path.expanduser('~/.ssh/guli_deploy')

# Windows 控制台按 utf-8 输出
if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8', errors='replace')


def run(cmds, timeout=120, pty=False):
    c = paramiko.SSHClient()
    c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    c.connect(HOST, 22, username=USER, key_filename=KEY,
              timeout=15, banner_timeout=15, auth_timeout=15,
              look_for_keys=False, allow_agent=False)
    rc_all = 0
    try:
        for cmd in cmds:
            print(f'\n$ {cmd}', flush=True)
            stdin, stdout, stderr = c.exec_command(cmd, timeout=timeout, get_pty=pty)
            out = stdout.read().decode('utf-8', errors='replace')
            err = stderr.read().decode('utf-8', errors='replace')
            rc = stdout.channel.recv_exit_status()
            if out.strip():
                print(out.rstrip())
            if err.strip():
                print('[stderr]', err.rstrip())
            print(f'[exit {rc}]', flush=True)
            rc_all = rc
    finally:
        c.close()
    return rc_all


if __name__ == '__main__':
    args = sys.argv[1:]
    pty = False
    if args and args[0] == '--pty':
        pty = True
        args = args[1:]
    timeout = int(os.environ.get('REMOTE_TIMEOUT', '120'))
    if not args:
        print(__doc__)
        sys.exit(1)
    sys.exit(run(args, timeout=timeout, pty=pty))
