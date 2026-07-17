"""gRPC 客户端 - 测试 RAG 服务"""
import asyncio
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

import grpc

from src.grpc_stubs import rag_service_pb2, rag_service_pb2_grpc
from config.nacos_config import NACOS_CONFIG


class RAGClient:
    def __init__(self, host: str = "127.0.0.1", port: int = None):
        port = port or NACOS_CONFIG["service_port"]
        self.address = f"{host}:{port}"
        self._channel = None

    @property
    def channel(self):
        if self._channel is None:
            self._channel = grpc.insecure_channel(self.address)
        return self._channel

    def ask(self, question: str) -> str:
        stub = rag_service_pb2_grpc.RAGServiceStub(self.channel)
        request = rag_service_pb2.QueryRequest(question=question)
        response = stub.Ask(request, timeout=30)
        return response.answer

    def ask_stream(self, question: str):
        """流式调用 AskStream RPC，逐块打印回答"""
        stub = rag_service_pb2_grpc.RAGServiceStub(self.channel)
        request = rag_service_pb2.QueryRequest(question=question)
        for chunk in stub.AskStream(request):
            yield chunk.chunk

    def close(self):
        if self._channel:
            self._channel.close()
            self._channel = None


async def main():
    client = RAGClient()

    questions = [
        "学分绩点怎么计算",
        "国家奖学金的金额是多少",
        "申请免听课程需要什么条件",
    ]

    for q in questions:
        print(f"问题: {q}")
        try:
            answer = client.ask(q)
            print(f"回答:\n{answer}")
        except Exception as e:
            print(f"错误: {e}")
        print("-" * 50)

    # 流式调用示例
    print("\n===== 流式调用演示 =====")
    print(f"问题: 学分绩点怎么计算")
    print("回答: ", end="", flush=True)
    try:
        for chunk in client.ask_stream("学分绩点怎么计算"):
            print(chunk, end="", flush=True)
        print()
    except Exception as e:
        print(f"\n错误: {e}")

    client.close()


if __name__ == "__main__":
    asyncio.run(main())
