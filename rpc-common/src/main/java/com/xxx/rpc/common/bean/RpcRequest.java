package com.xxx.rpc.common.bean;

/**
 * 封装 RPC 请求
 *
 * @author huangyong
 * @since 1.0.0
 */
public class RpcRequest {

    // 添加了请求ID，用于异步的情况下匹配请求和应答？
    private String     requestId;
    // 添加了服务的version
    private String     serviceVersion;

    // 其他都是正常基于函数调用的RPC的参数
    private String     interfaceName;
    private String     methodName;
    private Class<?>[] parameterTypes;
    private Object[]   parameters;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String className) {
        this.interfaceName = className;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
