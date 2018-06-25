package com.xxx.rpc.server;

import com.xxx.rpc.HandlerTask;
import com.xxx.rpc.common.bean.RpcRequest;
import com.xxx.rpc.common.executor.ExecuteUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * RPC 服务端处理器（用于处理 RPC 请求）
 *
 * @author huangyong
 * @since 1.0.0
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerHandler.class);

    private final Map<String, Object> handlerMap;

    private ExecuteUtil executor;

    public RpcServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
        executor = ExecuteUtil.getInstance("RpcServerHandlerExecutor", 10, 20, 100, 10);
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        // 提交到业务线程池处理，防止堵塞NettyIO线程
        executor.execute(new HandlerTask(request, handlerMap, ctx));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }
}
