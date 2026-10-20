from concurrent import futures
import grpc
import structlog
import document_pb2
import document_pb2_grpc
from ocr_engine import extract_text_from_bytes
from parser import parse_invoice_data

structlog.configure(
    processors=[
        structlog.processors.TimeStamper(fmt="%Y-%m-%d %H:%M:%S", utc=False),
        structlog.processors.add_log_level,
        structlog.dev.ConsoleRenderer()
    ],
    wrapper_class=structlog.make_filtering_bound_logger(20),
    logger_factory=structlog.PrintLoggerFactory(),
    cache_logger_on_first_use=False,
)

logger = structlog.get_logger()


class DocumentProcessorServicer(document_pb2_grpc.DocumentProcessorServicer):
    def ProcessDocument(self, request, context):
        logger.info(
            "Получен запрос на обработку документа",
            document_id=request.document_id,
            file_name=request.file_name,
            file_size=len(request.file_content),
            type_hint=request.document_type_hint,
        )

        text = extract_text_from_bytes(request.file_content, request.file_name)
        logger.debug("Распознанный текст", text=text)
        if not text:
            return document_pb2.ProccessResponse(
                success=False,
                errors=["Не удалось распознать текст (OCR вернул пустоту)"]
            )

        parsed = parse_invoice_data(text)

        response = document_pb2.ProcessResponse(
            success=len(parsed["errors"]) == 0,
            document_type=parsed["document_type"],
            number=parsed["number"] or "",
            date=parsed["date"] or "",
            inn=parsed["inn"] or "",
            amount=parsed["amount"] or 0.0,
            errors=parsed["errors"],
        )

        logger.info("Результат обработки", success=response.success, errors=response.errors)
        return response


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    document_pb2_grpc.add_DocumentProcessorServicer_to_server(
        DocumentProcessorServicer(), server
    )

    port = '50051'
    server.add_insecure_port(f'[::]:{port}')

    logger.info("gRPC сервер запущен", port=port)
    server.start()
    server.wait_for_termination()


if __name__ == '__main__':
    serve()
