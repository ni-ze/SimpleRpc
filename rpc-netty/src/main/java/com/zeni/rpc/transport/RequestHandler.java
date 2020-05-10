package com.zeni.rpc.transport;

import com.zeni.rpc.transport.Command.Command;

public interface RequestHandler {
    Command handler(Command requestCommand);

    int type();
}
