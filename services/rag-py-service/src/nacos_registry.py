# Nacos 服务注册与发现模块
import asyncio
import socket
from typing import Optional

try:
    from v2.nacos.common.client_config_builder import ClientConfigBuilder
    from v2.nacos.common.nacos_exception import NacosException
    from v2.nacos.naming.nacos_naming_service import NacosNamingService
    from v2.nacos.naming.model.naming_param import (
        RegisterInstanceParam,
        DeregisterInstanceParam,
        ListInstanceParam,
    )
    from v2.nacos.naming.model.instance import Instance
    _NACOS_AVAILABLE = True
except ImportError:
    _NACOS_AVAILABLE = False
    ClientConfigBuilder = None  # type: ignore
    NacosException = Exception  # type: ignore
    NacosNamingService = None  # type: ignore


def _get_local_ip() -> str:
    """获取本机 IP 地址"""
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
        s.close()
        return ip
    except OSError:
        return "127.0.0.1"


class NacosRegistry:
    """Nacos 服务注册/发现助手"""

    def __init__(
        self,
        server_address: str = "127.0.0.1:8848",
        namespace_id: str = "",
        group_name: str = "DEFAULT_GROUP",
        service_name: str = "rag-service",
        service_ip: Optional[str] = None,
        service_port: int = 50051,
    ):
        self.server_address = server_address
        self.namespace_id = namespace_id
        self.group_name = group_name
        self.service_name = service_name
        self.service_ip = service_ip or _get_local_ip()
        self.service_port = service_port
        self._naming_service: Optional[NacosNamingService] = None

    async def _get_naming_service(self) -> NacosNamingService:
        """获取或创建 NacosNamingService 实例"""
        if self._naming_service is None:
            config = (
                ClientConfigBuilder()
                .server_address(self.server_address)
                .namespace_id(self.namespace_id)
                .build()
            )
            try:
                self._naming_service = await NacosNamingService.create_naming_service(config)
            except NacosException as e:
                raise NacosException(
                    e.error_code,
                    f"无法连接 Nacos Server({self.server_address}): {e}. "
                    f"请确认 Nacos Server 已启动，且 gRPC 端口({int(self.server_address.rsplit(':',1)[1]) + 1000})可访问。"
                )
        return self._naming_service

    async def register(self) -> bool:
        """将当前服务实例注册到 Nacos"""
        if not _NACOS_AVAILABLE:
            print("[Nacos] Nacos SDK 不可用，跳过注册")
            return False
        naming = await self._get_naming_service()
        param = RegisterInstanceParam(
            service_name=self.service_name,
            group_name=self.group_name,
            ip=self.service_ip,
            port=self.service_port,
        )
        result = await naming.register_instance(param)
        if result:
            print(
                f"[Nacos] 服务注册成功: {self.service_name} "
                f"@ {self.service_ip}:{self.service_port}"
            )
            print(
                f"        Nacos 控制台: http://{self.server_address}/nacos → 服务管理 → 服务列表"
            )
        else:
            print(
                f"[Nacos] 服务注册失败: {self.service_name}，"
                f"请检查 Nacos 控制台日志。"
            )
        return result

    async def deregister(self) -> bool:
        """从 Nacos 注销当前服务实例"""
        if self._naming_service is None:
            return True
        naming = self._naming_service
        param = DeregisterInstanceParam(
            service_name=self.service_name,
            group_name=self.group_name,
            ip=self.service_ip,
            port=self.service_port,
        )
        result = await naming.deregister_instance(param)
        if result:
            print(f"[Nacos] 服务注销成功: {self.service_name}")
        return result

    async def discover(self) -> list[tuple[str, int]]:
        """
        从 Nacos 发现服务实例列表。

        Returns:
            [(ip, port), ...] 可用实例地址列表
        """
        naming = await self._get_naming_service()
        param = ListInstanceParam(
            service_name=self.service_name,
            group_name=self.group_name,
            healthy_only=True,
        )
        instances: list[Instance] = await naming.list_instances(param)
        return [(inst.ip, inst.port) for inst in instances if inst.healthy]

    async def close(self):
        """关闭 Nacos 连接"""
        if self._naming_service:
            await self._naming_service.shutdown()
