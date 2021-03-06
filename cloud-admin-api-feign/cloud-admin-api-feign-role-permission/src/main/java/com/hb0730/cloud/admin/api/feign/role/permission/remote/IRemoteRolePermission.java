package com.hb0730.cloud.admin.api.feign.role.permission.remote;

import com.hb0730.cloud.admin.api.feign.role.permission.remote.fallback.RemoteRolePermissionFallbackFactory;
import com.hb0730.cloud.admin.common.web.response.ResultJson;
import com.hb0730.cloud.admin.commons.feign.configuration.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static com.hb0730.cloud.admin.common.util.RequestMappingConstants.ROLE_PERMISSION_SERVER_REQUEST;
import static com.hb0730.cloud.admin.common.util.ServerNameConstants.ROLE_PERMISSION_SERVER;

/**
 * <p>
 * 远程调用角色权限
 * </P>
 *
 * @author bing_huang
 * @since V1.0
 */
@FeignClient(name = ROLE_PERMISSION_SERVER, path = ROLE_PERMISSION_SERVER_REQUEST, configuration = FeignConfiguration.class, fallbackFactory = RemoteRolePermissionFallbackFactory.class)
public interface IRemoteRolePermission {

    @PostMapping("/getPermission/roleId")
    ResultJson getPermissionIdByRoleIds(@RequestBody List<Long> roleIds);
}
