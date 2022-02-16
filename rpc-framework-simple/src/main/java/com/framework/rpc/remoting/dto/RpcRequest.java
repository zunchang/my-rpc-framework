package com.framework.rpc.remoting.dto;

import lombok.*;

import java.io.Serializable;

/**
 * 客户端请求类
 */

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@ToString
public class RpcRequest implements Serializable {
    // 序列号版本
    private static final long serialVersionUID=1905122041950251207L;
    // 请求ID
    private String requestId;
    // 接口名称
    private String interfaceName;
    // 方法名
    private String methodName;
    // 参数
    private Object[] parameters;
    // 参数类型
    private Class<?>[] paramTypes;
    // 版本号
    private String version;
    // 请求组
    private String group;

    //rpc服务名称
    public String getRpcServiceName(){
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
